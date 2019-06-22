package com.hermosaprogramacion.premium.androidmyrestaurant.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;


public class Restaurant{

	@SerializedName("result")
	private List<RestaurantItem> result;

	@SerializedName("succes")
	private boolean succes;

	public void setResult(List<RestaurantItem> result){
		this.result = result;
	}

	public List<RestaurantItem> getResult(){
		return result;
	}

	public void setSucces(boolean succes){
		this.succes = succes;
	}

	public boolean isSucces(){
		return succes;
	}

	@Override
 	public String toString(){
		return 
			"Restaurant{" + 
			"result = '" + result + '\'' + 
			",succes = '" + succes + '\'' + 
			"}";
		}
}