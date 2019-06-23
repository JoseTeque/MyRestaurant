package com.hermosaprogramacion.premium.androidmyrestaurant.database;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class LocalCartDataSource implements CartDataSource {

    private CartDao cartDao;

    public LocalCartDataSource(CartDao cartDao) {
        this.cartDao = cartDao;
    }

    @Override
    public Flowable<List<CartItem>> getAllCart(String userPhone, int restaurantId) {
        return cartDao.getAllCart(userPhone,restaurantId);
    }

    @Override
    public Single<Integer> countItemInCart(String userPhone, int restaurantId) {
        return cartDao.countItemInCart(userPhone,restaurantId);
    }

    @Override
    public Single<CartItem> sumPrice(int foodId, String userPhone, int restaurantId) {
        return cartDao.sumPrice(foodId,userPhone,restaurantId);
    }

    @Override
    public Completable inserOrReplaceAll(CartItem... cartItems) {
        return cartDao.inserOrReplaceAll(cartItems);
    }

    @Override
    public Single<Integer> updateCart(CartItem cartItems) {
        return cartDao.updateCart(cartItems);
    }

    @Override
    public Single<Integer> deleteCart(CartItem cartItem) {
        return cartDao.deleteCart(cartItem);
    }

    @Override
    public Single<Integer> cleanCart(String userPhone, int restaurantId) {
        return cartDao.cleanCart(userPhone,restaurantId);
    }
}
