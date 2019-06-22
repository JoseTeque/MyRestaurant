package com.hermosaprogramacion.premium.androidmyrestaurant.adapter;

import com.hermosaprogramacion.premium.androidmyrestaurant.model.RestaurantItem;

import java.util.List;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class BannerSliderAdapter extends SliderAdapter {

    private List<RestaurantItem> restaurantItems;

    public BannerSliderAdapter(List<RestaurantItem> restaurantItems) {
        this.restaurantItems = restaurantItems;
    }

    @Override
    public int getItemCount() {
        return restaurantItems.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
        imageSlideViewHolder.bindImageSlide(restaurantItems.get(position).getImage());
    }
}
