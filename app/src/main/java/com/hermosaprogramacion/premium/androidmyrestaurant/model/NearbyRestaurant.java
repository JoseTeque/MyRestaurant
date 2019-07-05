package com.hermosaprogramacion.premium.androidmyrestaurant.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;


public class NearbyRestaurant{

	@SerializedName("result")
	private List<NearbyRestaurantItem> result;

	@SerializedName("succes")
	private boolean succes;

	@SerializedName("message")
	private String message;

	public void setResult(List<NearbyRestaurantItem> result){
		this.result = result;
	}

	public List<NearbyRestaurantItem> getResult(){
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