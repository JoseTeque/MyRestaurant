package com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus;

import com.hermosaprogramacion.premium.androidmyrestaurant.model.FoodItem;

public class FoodDetailEvent {
    private boolean succes;
    private FoodItem food;

    public FoodDetailEvent(boolean succes, FoodItem food) {
        this.succes = succes;
        this.food = food;
    }

    public boolean isSucces() {
        return succes;
    }

    public void setSucces(boolean succes) {
        this.succes = succes;
    }

    public FoodItem getFood() {
        return food;
    }

    public void setFood(FoodItem food) {
        this.food = food;
    }
}

