package com.hermosaprogramacion.premium.androidmyrestaurant.model;


import com.google.gson.annotations.SerializedName;


public class BraintreeToken{

	@SerializedName("clientToken")
	private String clientToken;

	@SerializedName("success")
	private boolean success;

	public void setClientToken(String clientToken){
		this.clientToken = clientToken;
	}

	public String getClientToken(){
		return clientToken;
	}

	public void setSuccess(boolean success){
		this.success = success;
	}

	public boolean isSuccess(){
		return success;
	}

	@Override
 	public String toString(){
		return 
			"BraintreeToken{" + 
			"clientToken = '" + clientToken + '\'' + 
			",success = '" + success + '\'' + 
			"}";
		}
}