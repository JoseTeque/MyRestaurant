package com.hermosaprogramacion.premium.androidmyrestaurant.model;

import java.util.List;

public class UserModel {

    private boolean succes;
    private String message;
    private List<User> result;

    public UserModel() {
    }

    public UserModel(boolean succes, String message, List<User> result) {
        this.succes = succes;
        this.message = message;
        this.result = result;
    }

    public boolean isSucces() {
        return succes;
    }

    public void setSucces(boolean succes) {
        this.succes = succes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<User> getResult() {
        return result;
    }

    public void setResult(List<User> result) {
        this.result = result;
    }
}
