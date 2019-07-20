package com.hermosaprogramacion.premium.androidmyrestaurant.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FavoriteOnLyIdModel {
    @SerializedName("result")
    private List<FavoriteOnlyId> result;

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    public List<FavoriteOnlyId> getResult() {
        return result;
    }

    public void setResult(List<FavoriteOnlyId> result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
