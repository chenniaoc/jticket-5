package com.jiepang.util;

public class CommonUtils {
	
	public static String generateOrderNo(){
		String result = "OID" + System.currentTimeMillis();
				
		return result;
	}
}
