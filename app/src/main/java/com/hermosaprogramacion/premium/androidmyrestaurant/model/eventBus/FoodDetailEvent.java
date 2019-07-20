package com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus;

import com.hermosaprogramacion.premium.androidmyrestaurant.model.FoodItem;

public class FoodDetailEvent {
    private boolean success;
    private FoodItem food;

    public FoodDetailEvent(boolean success, FoodItem food) {
        this.success = success;
        this.food = food;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public FoodItem getFood() {
        return food;
    }

    public void setFood(FoodItem food) {
        this.food = food;
    }
}

