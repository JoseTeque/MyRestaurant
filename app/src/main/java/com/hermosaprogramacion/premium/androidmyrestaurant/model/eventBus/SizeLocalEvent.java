package com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus;

import com.hermosaprogramacion.premium.androidmyrestaurant.model.MenuItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.SizeItem;

import java.util.List;

public class SizeLocalEvent {
    private boolean succes;
    private List<SizeItem> sizeList;

    public SizeLocalEvent(boolean succes, List<SizeItem> sizeList) {
        this.succes = succes;
        this.sizeList = sizeList;
    }

    public boolean isSucces() {
        return succes;
    }

    public void setSucces(boolean succes) {
        this.succes = succes;
    }

    public List<SizeItem> getSizeList() {
        return sizeList;
    }

    public void setSizeList(List<SizeItem> sizeList) {
        this.sizeList = sizeList;
    }
}
