package com.jiepang.model;

public class Order {
	
	private String id;
	
	private String[] identifyNo;
	
	private String trainDate;
	
	private String[] seatNo;
	
	private String trainNo;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

}
