package com.hermosaprogramacion.premium.androidmyrestaurant.model;


import com.google.gson.annotations.SerializedName;

public class CreateOrderItem {

	@SerializedName("orderNumber")
	private int orderNumber;

	public void setOrderNumber(int orderNumber){
		this.orderNumber = orderNumber;
	}

	public int getOrderNumber(){
		return orderNumber;
	}

}