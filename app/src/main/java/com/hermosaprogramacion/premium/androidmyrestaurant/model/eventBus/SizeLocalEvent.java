package com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus;

import com.hermosaprogramacion.premium.androidmyrestaurant.model.MenuItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.SizeItem;

import java.util.List;

public class SizeLocalEvent {
    private boolean success;
    private List<SizeItem> sizeList;

    public SizeLocalEvent(boolean success, List<SizeItem> sizeList) {
        this.success = success;
        this.sizeList = sizeList;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<SizeItem> getSizeList() {
        return sizeList;
    }

    public void setSizeList(List<SizeItem> sizeList) {
        this.sizeList = sizeList;
    }
}
