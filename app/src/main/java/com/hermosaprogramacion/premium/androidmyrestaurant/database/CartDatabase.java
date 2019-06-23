package com.hermosaprogramacion.premium.androidmyrestaurant.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(version = 1, entities = CartItem.class,exportSchema = false)
public abstract class CartDatabase extends RoomDatabase {

    private static CartDatabase instance;

    public abstract CartDao cartDao();

    public static CartDatabase getInstance(Context context) {

        if (instance== null)
            instance= Room.databaseBuilder(context,CartDatabase.class,"MyRestaurantCart").build();
        return instance;
    }
}
