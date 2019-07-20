package com.hermosaprogramacion.premium.androidmyrestaurant.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;


public class Menu{

	@SerializedName("result")
	private List<MenuItem> result;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public List<MenuItem> getResult() {
		return result;
	}

	public void setResult(List<MenuItem> result) {
		this.result = result;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}