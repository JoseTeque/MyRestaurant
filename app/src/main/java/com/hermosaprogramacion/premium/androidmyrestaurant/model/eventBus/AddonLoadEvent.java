package com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus;

import com.hermosaprogramacion.premium.androidmyrestaurant.model.AddonItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.SizeItem;

import java.util.List;

public class AddonLoadEvent {

    private boolean succes;
    private List<AddonItem> addonList;

    public AddonLoadEvent(boolean succes, List<AddonItem> addonList) {
        this.succes = succes;
        this.addonList = addonList;
    }

    public boolean isSucces() {
        return succes;
    }

    public void setSucces(boolean succes) {
        this.succes = succes;
    }

    public List<AddonItem> getAddonList() {
        return addonList;
    }

    public void setAddonList(List<AddonItem> addonList) {
        this.addonList = addonList;
    }
}
