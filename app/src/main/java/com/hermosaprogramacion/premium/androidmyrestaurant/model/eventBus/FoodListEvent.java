package com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus;

import com.hermosaprogramacion.premium.androidmyrestaurant.model.FoodItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.Menu;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.MenuItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.RestaurantItem;

public class FoodListEvent {
    private boolean succes;
    private MenuItem menuItem;


    public FoodListEvent(boolean succes, MenuItem menuItem) {
        this.succes = succes;
        this.menuItem = menuItem;
    }

    public boolean isSucces() {
        return succes;
    }

    public void setSucces(boolean succes) {
        this.succes = succes;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

}
