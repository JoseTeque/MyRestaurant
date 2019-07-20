package com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus;

import com.hermosaprogramacion.premium.androidmyrestaurant.model.AddonItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.SizeItem;

import java.util.List;

public class AddonLoadEvent {

    private boolean success;
    private List<AddonItem> addonList;

    public AddonLoadEvent(boolean success, List<AddonItem> addonList) {
        this.success = success;
        this.addonList = addonList;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<AddonItem> getAddonList() {
        return addonList;
    }

    public void setAddonList(List<AddonItem> addonList) {
        this.addonList = addonList;
    }
}
