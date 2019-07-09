package com.hermosaprogramacion.premium.androidmyrestaurant.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;


public class MaxOrder{

	@SerializedName("result")
	private List<MaxOrderItem> result;

	@SerializedName("succes")
	private boolean succes;

	@SerializedName("message")
	private String message;

	public void setResult(List<MaxOrderItem> result){
		this.result = result;
	}

	public List<MaxOrderItem> getResult(){
		return result;
	}

	public void setSucces(boolean succes){
		this.succes = succes;
	}

	public boolean isSucces(){
		return succes;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}