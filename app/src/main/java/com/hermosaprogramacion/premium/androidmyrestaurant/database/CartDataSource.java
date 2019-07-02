package com.hermosaprogramacion.premium.androidmyrestaurant.database;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface CartDataSource {

    Flowable<List<CartItem>> getAllCart(String userPhone, int restaurantId);

    Single<Integer> countItemInCart(String userPhone, int restaurantId);

    Single<Long> sumPrice(String userPhone, int restaurantId);

    Completable inserOrReplaceAll(CartItem... cartItems);

    Single<Integer> updateCart(CartItem cartItems);

    Single<Integer> deleteCart(CartItem cartItem);

    Single<Integer> cleanCart(String userPhone, int restaurantId);
}
