package com.hermosaprogramacion.premium.androidmyrestaurant.common;

import com.hermosaprogramacion.premium.androidmyrestaurant.model.AddonItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.FavoriteItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.FavoriteOnlyId;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.RestaurantItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.User;

import java.util.HashSet;
import java.util.List;
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
    public static List<FavoriteOnlyId> currentFavOfRestaurant;

    public static boolean checkFavorite(int id) {
        boolean result = false;
        for (FavoriteOnlyId item : currentFavOfRestaurant)
            if (item.getFoodId() == id)
            {
                result = true;
            }
        return result;
    }

    public static void removeFavorite(int id) {

        for (FavoriteOnlyId item : currentFavOfRestaurant)
            if (item.getFoodId() == id)
            {
                currentFavOfRestaurant.remove(item);
            }

    }

    public static String converStatusToString(int orderStatus) {

        switch (orderStatus)
        {
            case 0:
                return "Placed";
            case 1:
                return "Shipping";
            case 2:
                return "shipped";
            case -1:
                return "cancelled";
                default:
                    return "cancelled";
        }
    }
}
