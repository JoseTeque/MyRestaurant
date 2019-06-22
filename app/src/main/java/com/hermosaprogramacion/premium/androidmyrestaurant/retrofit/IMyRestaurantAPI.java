package com.hermosaprogramacion.premium.androidmyrestaurant.retrofit;

import com.hermosaprogramacion.premium.androidmyrestaurant.model.Menu;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.Restaurant;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.UpdateUserModel;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.UserModel;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IMyRestaurantAPI {

    @GET("user")
    Observable<UserModel> getUser(@Query("key") String key,
                                  @Query("fbid") String fbid);

    @GET("restaurant")
    Observable<Restaurant> getRestaurant(@Query("key") String key);

    @GET("menu")
    Observable<Menu> getMenu(@Query("key") String key, @Query("restaurantId") int restaurantId);

    @POST("user")
    @FormUrlEncoded
    Observable<UpdateUserModel> postUser(@Field("key") String key,
                                         @Field("fbid") String fbid,
                                         @Field("userPhone") String phone,
                                         @Field("userName") String name,
                                         @Field("userAddress") String address);




}
