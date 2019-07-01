package com.hermosaprogramacion.premium.androidmyrestaurant.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FavoriteOnLyIdModel {
    @SerializedName("result")
    private List<FavoriteOnlyId> result;

    @SerializedName("succes")
    private boolean succes;

    @SerializedName("message")
    private String message;

    public List<FavoriteOnlyId> getResult() {
        return result;
    }

    public void setResult(List<FavoriteOnlyId> result) {
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
}
