package com.nihaopay.sdk.pay.demo;

import java.util.Properties;

public class NihaopayConfig {

	public static  String URL = "";
	public static String unionMode = "";
	
	static{
//		Properties props = ResourceUtils.getResourceAsProperty("nihaopay/nihaopay.properties",
//				NihaopayConfig.class.getClassLoader());
//		URL  = props.getProperty("nihaopay.pgw.url");
//		unionMode = props.getProperty("nihaopay.union.mode");
		URL="https://apitest.nihaopay.com/v1.2/transactions/apppay";
		unionMode="01";
	}
	
}
