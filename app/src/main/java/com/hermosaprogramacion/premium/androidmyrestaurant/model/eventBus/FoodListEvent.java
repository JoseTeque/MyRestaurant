package com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus;

import com.hermosaprogramacion.premium.androidmyrestaurant.model.FoodItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.Menu;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.MenuItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.RestaurantItem;

public class FoodListEvent {
    private boolean success;
    private MenuItem menuItem;

    public FoodListEvent(boolean success, MenuItem menuItem) {
        this.success = success;
        this.menuItem = menuItem;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }
}
