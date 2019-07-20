package com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus;

import com.hermosaprogramacion.premium.androidmyrestaurant.model.RestaurantItem;

public class MenuItemEvent {
    private boolean success;
    private RestaurantItem restaurantItem;

    public MenuItemEvent(boolean success, RestaurantItem restaurantItem) {
        this.success = success;
        this.restaurantItem = restaurantItem;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public RestaurantItem getRestaurantItem() {
        return restaurantItem;
    }

    public void setRestaurantItem(RestaurantItem restaurantItem) {
        this.restaurantItem = restaurantItem;
    }
}
