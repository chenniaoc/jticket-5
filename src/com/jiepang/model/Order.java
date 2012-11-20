package com.jiepang.model;

public class Order {
	
	private String orderNo;
	
	private String[] identifyNo;
	
	private String trainDate;
	
	private String[] seatNo;
	
	private String trainNo;


	public String[] getIdentifyNo() {
		return identifyNo;
	}

	public void setIdentifyNo(String[] identifyNo) {
		this.identifyNo = identifyNo;
	}

	public String getTrainDate() {
		return trainDate;
	}

	public void setTrainDate(String trainDate) {
		this.trainDate = trainDate;
	}

	public String[] getSeatNo() {
		return seatNo;
	}

	public void setSeatNo(String[] seatNo) {
		this.seatNo = seatNo;
	}

	public String getTrainNo() {
		return trainNo;
	}

	public void setTrainNo(String trainNo) {
		this.trainNo = trainNo;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

}
