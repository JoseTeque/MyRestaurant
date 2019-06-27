package com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus;

import com.hermosaprogramacion.premium.androidmyrestaurant.model.AddonItem;

public class AddonEventChange {
    private boolean isAddon;
    private AddonItem addonItem;

    public AddonEventChange(boolean isAddon, AddonItem addonItem) {
        this.isAddon = isAddon;
        this.addonItem = addonItem;
    }

    public boolean isAddon() {
        return isAddon;
    }

    public void setAddon(boolean addon) {
        isAddon = addon;
    }

    public AddonItem getAddonItem() {
        return addonItem;
    }

    public void setAddonItem(AddonItem addonItem) {
        this.addonItem = addonItem;
    }
}
