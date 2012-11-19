package com.jiepang.util;

public class KeyUtils {
	
	/** user's Identify No*/
	static final String UID = "uid:";
	
	/** Train Number */
	static final String TRAIN_NUMBER = "tno:";
	
	public static String order(String uid,String tno){
		return UID + uid + "tno:"+ tno +"order";
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
	
}
