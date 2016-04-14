package com.pvi.ap.reader.common.beans;

public class RankType {
	private String type;
	private String id;
	private String name;
	private String orderNo;

	public RankType(String type, String id, String name, String orderNo) {
		super();
		this.type = type;
		this.id = id;
		this.name = name;
		this.orderNo = orderNo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
}