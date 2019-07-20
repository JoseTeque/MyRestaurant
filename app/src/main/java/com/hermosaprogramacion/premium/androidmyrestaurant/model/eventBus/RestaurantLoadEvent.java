package com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus;

import com.hermosaprogramacion.premium.androidmyrestaurant.model.RestaurantItem;

import java.util.List;

public class RestaurantLoadEvent {

    private boolean success;
    private List<RestaurantItem> restaurantList;
    private String message;

    public RestaurantLoadEvent(boolean success, List<RestaurantItem> restaurantList) {
        this.success = success;
        this.restaurantList = restaurantList;
    }

    public RestaurantLoadEvent(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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
