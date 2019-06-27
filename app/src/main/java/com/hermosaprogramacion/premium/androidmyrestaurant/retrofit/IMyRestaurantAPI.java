package com.hermosaprogramacion.premium.androidmyrestaurant.retrofit;

import com.hermosaprogramacion.premium.androidmyrestaurant.model.Addon;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.Food;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.Menu;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.Restaurant;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.Size;
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

    @GET("food")
    Observable<Food> getFood(@Query("key") String key, @Query("menuId") int menuId);

    @GET("searchfood")
    Observable<Food> getSearch(@Query("key") String key, @Query("foodName") String nameFood);

    @GET("size")
    Observable<Size> getSize(@Query("key") String key, @Query("foodId") int foodId);

    @GET("addon")
    Observable<Addon> getAddon(@Query("key") String key, @Query("foodId") int foodId);

    //POST

    @POST("user")
    @FormUrlEncoded
    Observable<UpdateUserModel> postUser(@Field("key") String key,
                                         @Field("fbid") String fbid,
                                         @Field("userPhone") String phone,
                                         @Field("userName") String name,
                                         @Field("userAddress") String address);




}
