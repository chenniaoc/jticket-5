package com.jiepang.util;

import java.util.HashSet;
import java.util.Set;

public class CommonUtils {
	
	public static String generateOrderNo(){
		String result = "OID" + System.currentTimeMillis();
				
		return result;
	}
	
	
	public static String[] unifyParams(String[] params){
		Set<String> a = new HashSet<String>();
		for(String p :params){
			a.add(p);
		}
		return a.toArray(new String[0]);
	}
}
