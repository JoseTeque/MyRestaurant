package com.hermosaprogramacion.premium.androidmyrestaurant.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;


public class Menu{

	@SerializedName("result")
	private List<MenuItem> result;

	@SerializedName("succes")
	private boolean succes;

	@SerializedName("message")
	private String message;



	public void setResult(List<MenuItem> result){
		this.result = result;
	}

	public List<MenuItem> getResult(){
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