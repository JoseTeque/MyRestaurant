package com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus;

import com.hermosaprogramacion.premium.androidmyrestaurant.model.RestaurantItem;

public class MenuItemEvent {
    private boolean succes;
    private RestaurantItem restaurantItem;

    public MenuItemEvent(boolean succes, RestaurantItem restaurantItem) {
        this.succes = succes;
        this.restaurantItem = restaurantItem;
    }

    public boolean isSucces() {
        return succes;
    }

    public void setSucces(boolean succes) {
        this.succes = succes;
    }

    public RestaurantItem getRestaurantItem() {
        return restaurantItem;
    }

    public void setRestaurantItem(RestaurantItem restaurantItem) {
        this.restaurantItem = restaurantItem;
    }
}
