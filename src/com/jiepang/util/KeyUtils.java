package com.jiepang.util;

public class KeyUtils {
	
	/** user's Identify No*/
	static final String UID = "uid:";
	
	/** Train Number */
	static final String TRAIN_NUMBER = "tno:";
	
	public static String order(String uid,String tno){
		return UID + uid + "tno:"+ tno +"order";
	}
	
	public static String cancleOrder(String uid,String tno){
		return UID + uid + "tno:"+ tno +"cancle";
	}
	
	/**
	 * 
	 * @param trainNo 车次
	 * @return 此车次的余票对应redis的KEY
	 */
	public static String remain(String trainNo,String date){
		return TRAIN_NUMBER + trainNo +"date:"+ date +":remain";
	}
	
	
	/*
	 *global tickets information
	 */
	public static String globalTicketNo(){
		return "global:tno";
	}
	
	/*
	 *global users information
	 */
	public static String globalUid(){
		return "global:uid";
	}
	
	/**
	 * "uid:22tno:T10order"
	 * will return String[] 22,T10
	 * @param orderKey
	 * @return
	 */
	public static String[] getSplitOrderKey(String orderKey){
		String[] result = new String[2];
		
		int firstColon = orderKey.indexOf(":", 0);
		int secondColon = orderKey.indexOf("tno:", firstColon + 1);
		String uid = orderKey.substring(firstColon + 1, secondColon);
		String tno = orderKey.substring(secondColon + 4, orderKey.indexOf("order"));
		result[0] = uid;
		result[1] = tno;
		return result;
	}
	
}
