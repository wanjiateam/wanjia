package com.wanjia.utils;

/**
	 * 记录 各个订单状态的信息
	 */
public class OrderStateInfo {

	private int type;
	private double totalPrice;
	private String id;

	public OrderStateInfo(int type, double totalPrice, String id) {

		this.type = type;
		this.totalPrice = totalPrice;
		this.id = id;

	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getId() {
		return id;
	}
}
