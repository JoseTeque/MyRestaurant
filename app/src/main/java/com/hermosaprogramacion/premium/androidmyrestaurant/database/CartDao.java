package com.hermosaprogramacion.premium.androidmyrestaurant.database;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface CartDao {

    //we only load cart by restaurantId
    //because each restaurant id will have order recepit different
    //because each restaurant have different link payment, so we can´t make 1 cart for all
    @Query("SELECT * FROM Cart WHERE userPhone=:userPhone AND restaurantId=:restaurantId")
    Flowable<List<CartItem>> getAllCart(String userPhone, int restaurantId);

    @Query("SELECT COUNT(*) from Cart WHERE userPhone=:userPhone AND restaurantId=:restaurantId")
    Single<Integer> countItemInCart(String userPhone, int restaurantId);

    @Query("SELECT * from Cart WHERE foodId=:foodId AND userPhone=:userPhone AND restaurantId=:restaurantId")
    Single<CartItem> sumPrice(int foodId,String userPhone, int restaurantId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable inserOrReplaceAll(CartItem... cartItems);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Single<Integer> updateCart(CartItem cartItems);

    @Delete
    Single<Integer> deleteCart(CartItem cartItem);

    @Query("DELETE FROM Cart WHERE  userPhone=:userPhone AND restaurantId=:restaurantId")
    Single<Integer> cleanCart(String userPhone, int restaurantId);
}
