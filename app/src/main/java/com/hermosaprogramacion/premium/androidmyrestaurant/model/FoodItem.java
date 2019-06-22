package com.hermosaprogramacion.premium.androidmyrestaurant.model;


import com.google.gson.annotations.SerializedName;


public class FoodItem {

	@SerializedName("image")
	private String image;

	@SerializedName("isSize")
	private boolean isSize;

	@SerializedName("price")
	private float price;

	@SerializedName("name")
	private String name;

	@SerializedName("isAddon")
	private boolean isAddon;

	@SerializedName("description")
	private String description;

	@SerializedName("discount")
	private int discount;

	@SerializedName("id")
	private int id;

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setIsSize(boolean isSize){
		this.isSize = isSize;
	}

	public boolean getIsSize(){
		return isSize;
	}

	public void setPrice(int price){
		this.price = price;
	}

	public float getPrice(){
		return price;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setIsAddon(boolean isAddon){
		this.isAddon = isAddon;
	}

	public boolean getIsAddon(){
		return isAddon;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setDiscount(int discount){
		this.discount = discount;
	}

	public int getDiscount(){
		return discount;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}


}