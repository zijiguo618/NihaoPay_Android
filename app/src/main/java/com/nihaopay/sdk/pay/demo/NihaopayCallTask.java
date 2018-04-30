package com.nihaopay.sdk.pay.demo;

import com.alipay.sdk.app.PayTask;

import android.app.Activity;

public class NihaopayCallTask {

	private PayTask payTask;
	
	public NihaopayCallTask(Activity fragmentActivity){
		payTask = new PayTask(fragmentActivity);
	}
	
	public String pay(String payInfo){
		
		String result = payTask.pay(payInfo, true);
		return result;
	}
	
}


