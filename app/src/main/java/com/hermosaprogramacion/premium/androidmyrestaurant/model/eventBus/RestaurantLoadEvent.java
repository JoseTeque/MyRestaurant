package com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus;

import com.hermosaprogramacion.premium.androidmyrestaurant.model.RestaurantItem;

import java.util.List;

public class RestaurantLoadEvent {

    private boolean succes;
    private List<RestaurantItem> restaurantList;
    private String message;

    public RestaurantLoadEvent(boolean succes, List<RestaurantItem> restaurantList) {
        this.succes = succes;
        this.restaurantList = restaurantList;
    }

    public RestaurantLoadEvent(boolean succes, String message) {
        this.succes = succes;
        this.message = message;
    }

    public boolean isSucces() {
        return succes;
    }

    public void setSucces(boolean succes) {
        this.succes = succes;
    }

    public List<RestaurantItem> getRestaurantList() {
        return restaurantList;
    }

    public void setRestaurantList(List<RestaurantItem> restaurantList) {
        this.restaurantList = restaurantList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
