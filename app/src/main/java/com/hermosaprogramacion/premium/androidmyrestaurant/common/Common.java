package com.hermosaprogramacion.premium.androidmyrestaurant.common;

import com.hermosaprogramacion.premium.androidmyrestaurant.model.AddonItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.RestaurantItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.User;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class Common {

    public static final String API_RESTAURANT_ENDPOINT = "http://192.168.8.103:3000/";
    public static final String API_KEY = "1234"; // we will set hard code API key now, but i will show you now to secure it with firebase remote config soon
    public static final int DEFAULT_COLUM_COUNT = 0;
    public static final int FULL_WIDTH_COLUM = 1 ;

    public static User currentUser;
    public static RestaurantItem currentRestaurant;
    public static Set<AddonItem> addonList = new HashSet<>();
}
