package com.hermosaprogramacion.premium.androidmyrestaurant.model;


import com.google.gson.annotations.SerializedName;

public class MaxOrderItem {

	@SerializedName("maxRowNum")
	private int maxRowNum;

	public void setMaxRowNum(int maxRowNum){
		this.maxRowNum = maxRowNum;
	}

	public int getMaxRowNum(){
		return maxRowNum;
	}


}