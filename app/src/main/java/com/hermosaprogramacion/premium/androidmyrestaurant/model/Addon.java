package com.hermosaprogramacion.premium.androidmyrestaurant.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;


public class Addon{

	@SerializedName("result")
	private List<AddonItem> result;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public List<AddonItem> getResult() {
		return result;
	}

	public void setResult(List<AddonItem> result) {
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