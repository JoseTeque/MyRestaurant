package com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus;

public class SendTotalCashEvent {
    private String cash;

    public SendTotalCashEvent(String cash) {
        this.cash = cash;
    }

    public String getCash() {
        return cash;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }
}
