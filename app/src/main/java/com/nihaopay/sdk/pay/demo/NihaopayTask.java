package com.nihaopay.sdk.pay.demo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import com.alipay.sdk.app.PayTask;
import com.unionpay.UPPayAssistEx;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.support.v4.content.ContextCompat;

public class NihaopayTask {

	private PayTask payTask;
	private Activity activity;
	 /*****************************************************************
     * mMode参数解释： "00" - 启动银联正式环境 "01" - 连接银联测试环境
     *****************************************************************/
//    private final String mMode = "01";
	
	public NihaopayTask(Activity activity){
		this.activity = activity;
		payTask = new PayTask(activity);
	}
	
	public String pay(String orderInfo, String token){
		Map<String, String> map = splitMapString(orderInfo, "&");
//		
		String vendor = map.get("vendor");
		
		String mytoken = "Bearer "+ token;
		
		String response = doPostQueryCmd(NihaopayConfig.URL, orderInfo, "utf-8", mytoken);
		if(response == null || response == ""){
			
			return "resultStatus={" + 0 + "};memo={" + 0
					+ "};result={" + 0 + "}";
		}
		AppResponse json = (AppResponse)NihaopayJSONUtils.toObject(response, AppResponse.class);
		
//		String vendor = json.getVendor();
		String result = "";
		if(vendor == null || "alipay".equals(vendor)){
			String payInfo = json.getOrderInfo();
			result = payTask.pay(payInfo, true);
		}else if("unionpay".equals(vendor)){

			 Looper.prepare();
			 String tn = json.getOrderInfo();
			Logger.i("log", "run unionpay");
			if(UPPayAssistEx.checkInstalled(activity)){
				Logger.i("log", "running unionpay");
				UPPayAssistEx.startPay(activity, null, null, tn, Config.Unionmode);
			}else{
				Logger.i("log", "installing unionpay");
				UPPayAssistEx.installUPPayPlugin(activity);

			}
//			 UPPayAssistEx.startPay(activity, null, null, tn, Config.Unionmode);
			 Looper.loop();// 进入loop中的循环，查看消息队列
		}else{
			Logger.error("not a reasonable vendor " + vendor);
		}
		
		return result;
	}

	public static String doPostQueryCmd(String strURL, String req, String charset, String token) {

		Logger.info(">>>sendUrl : " + strURL);
		Logger.debug(">>>send data : " + req+"   "+Manifest.permission.READ_PHONE_STATE);

		String result = null;
//		BufferedInputStream in = null;
		BufferedInputStream in=null;
		BufferedOutputStream out = null;
		try {

			URL url = new URL(strURL);
//			HttpURLConnection l;
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
//			URLConnection con = url.openConnection();
			if (con instanceof HttpsURLConnection) {
				((HttpsURLConnection) con).setHostnameVerifier(new HostnameVerifier() {
					@Override
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				});
			}
			con.addRequestProperty("Authorization", token);
			con.setUseCaches(false);
			con.setDoInput(true);
			con.setDoOutput(true);
			con.connect();
			out = new BufferedOutputStream(con.getOutputStream());

			byte outBuf[] = req.getBytes(charset);
			out.write(outBuf);
			out.close();
			Logger.info(">>>data send done!"+con.getResponseCode());
			Logger.info(">>>data received empty!"+con.getResponseMessage());
			in = new BufferedInputStream(con.getInputStream());
			result=ReadByteStream(in,charset);
			in.close();

			Logger.info(">>>receive data : " + result);
			if (result == null)
				return "";
			else
				return result;
		}
		catch (Exception ex) {
			Logger.error("connect bank error :" + ex);
			return "";
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	private static String ReadByteStream(BufferedInputStream in, String charset) throws IOException {
		LinkedList<Mybuf> bufList = new LinkedList<Mybuf>();
		int size = 0;
		byte buf[];
		do {
			buf = new byte[128];
			int num = in.read(buf);
			if (num == -1)
				break;
			size += num;
			bufList.add(new Mybuf(buf, num));
		} while (true);
		buf = new byte[size];
		int pos = 0;
		for (ListIterator<Mybuf> p = bufList.listIterator(); p.hasNext();) {
			Mybuf b = p.next();
			for (int i = 0; i < b.size;) {
				buf[pos] = b.buf[i];
				i++;
				pos++;
			}

		}

		return new String(buf, charset);
	}
	
	public static Map<String, String> splitMapString(String mapString, String delimeter) {
		String[] nameValuePairs = mapString.split(Pattern.quote(delimeter));
		Map<String, String> orderInfo = new TreeMap<String, String>();
		for (String nameValuePair : nameValuePairs) {
			int index = nameValuePair.indexOf("=");
			String name = nameValuePair.substring(0, index);
			String value = nameValuePair.substring(index + 1);
			orderInfo.put(name, value);
		}
		return orderInfo;
	}
}

class Mybuf {

	public byte buf[];
	public int size;

	public Mybuf(byte b[], int s) {
		buf = b;
		size = s;
	}
}

