package com.hermosaprogramacion.premium.androidmyrestaurant.model;


import com.google.gson.annotations.SerializedName;

public class MenuItem {

	@SerializedName("image")
	private String image;

	@SerializedName("address")
	private String address;

	@SerializedName("lng")
	private double lng;

	@SerializedName("userOwner")
	private int userOwner;

	@SerializedName("phone")
	private String phone;

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private int id;

	@SerializedName("paymentUrl")
	private String paymentUrl;

	@SerializedName("lat")
	private double lat;

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setAddress(String address){
		this.address = address;
	}

	public String getAddress(){
		return address;
	}

	public void setLng(double lng){
		this.lng = lng;
	}

	public double getLng(){
		return lng;
	}

	public void setUserOwner(int userOwner){
		this.userOwner = userOwner;
	}

	public int getUserOwner(){
		return userOwner;
	}

	public void setPhone(String phone){
		this.phone = phone;
	}

	public String getPhone(){
		return phone;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setPaymentUrl(String paymentUrl){
		this.paymentUrl = paymentUrl;
	}

	public String getPaymentUrl(){
		return paymentUrl;
	}

	public void setLat(double lat){
		this.lat = lat;
	}

	public double getLat(){
		return lat;
	}


}