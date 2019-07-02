package com.hermosaprogramacion.premium.androidmyrestaurant.interfac;

import android.view.View;

public interface IonImageViewAdapterClickListener {
    void onCalcularPriceListener(View view, int position, boolean isDecreace, boolean isDelete);
}
