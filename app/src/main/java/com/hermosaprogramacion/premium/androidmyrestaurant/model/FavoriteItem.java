package com.hermosaprogramacion.premium.androidmyrestaurant.model;


import com.google.gson.annotations.SerializedName;


public class FavoriteItem {

	@SerializedName("foodName")
	private String foodName;

	@SerializedName("restaurantName")
	private String restaurantName;

	@SerializedName("price")
	private Double price;

	@SerializedName("foodId")
	private int foodId;

	@SerializedName("fbid")
	private String fbid;

	@SerializedName("foodImage")
	private String foodImage;

	@SerializedName("restaurantId")
	private int restaurantId;

	public void setFoodName(String foodName){
		this.foodName = foodName;
	}

	public String getFoodName(){
		return foodName;
	}

	public void setRestaurantName(String restaurantName){
		this.restaurantName = restaurantName;
	}

	public String getRestaurantName(){
		return restaurantName;
	}

	public void setPrice(Double price){
		this.price = price;
	}

	public Double getPrice(){
		return price;
	}

	public void setFoodId(int foodId){
		this.foodId = foodId;
	}

	public int getFoodId(){
		return foodId;
	}

	public void setFbid(String fbid){
		this.fbid = fbid;
	}

	public String getFbid(){
		return fbid;
	}

	public void setFoodImage(String foodImage){
		this.foodImage = foodImage;
	}

	public String getFoodImage(){
		return foodImage;
	}

	public void setRestaurantId(int restaurantId){
		this.restaurantId = restaurantId;
	}

	public int getRestaurantId(){
		return restaurantId;
	}
}