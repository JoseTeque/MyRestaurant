package com.hermosaprogramacion.premium.androidmyrestaurant.retrofit;

import com.hermosaprogramacion.premium.androidmyrestaurant.model.Addon;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.CreateOrder;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.Favorite;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.FavoriteItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.FavoriteOnLyIdModel;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.Food;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.MaxOrder;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.Menu;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.NearbyRestaurant;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.Order;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.PostFavorite;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.Restaurant;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.Size;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.UpdateOrder;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.UpdateUserModel;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.UserModel;

import io.reactivex.Observable;
import retrofit2.http.DELETE;
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

    @GET("restaurantById")
    Observable<Restaurant> getRestaurantById(@Query("key") String key, @Query("restaurantId") String restaurantId);


    @GET("nearbyrestaurant")
    Observable<NearbyRestaurant> getNearbyrestaurant(@Query("key") String key, @Query("lat") Double lat, @Query("lng") Double lng, @Query("distance") int distance);

    @GET("menu")
    Observable<Menu> getMenu(@Query("key") String key, @Query("restaurantId") int restaurantId);

    @GET("food")
    Observable<Food> getFood(@Query("key") String key, @Query("menuId") int menuId);

    @GET("foodById")
    Observable<Food> getFoodById(@Query("key") String key, @Query("foodId") int foodId);

    @GET("searchfood")
    Observable<Food> getSearch(@Query("key") String key, @Query("foodName") String nameFood);

    @GET("size")
    Observable<Size> getSize(@Query("key") String key, @Query("foodId") int foodId);

    @GET("addon")
    Observable<Addon> getAddon(@Query("key") String key, @Query("foodId") int foodId);

    @GET("favorite")
    Observable<Favorite> getFavorite(@Query("key") String key, @Query("fbid") String fbid);

    @GET("favoriteByRestaurant")
    Observable<FavoriteOnLyIdModel> getFavoriteByRestaurant(@Query("key") String key, @Query("fbid") String fbid, @Query("restaurantId") int restaurantId);

    @GET("order")
    Observable<Order> getOrder(@Query("key") String key,
                               @Query("orderFBID") String orderFBID,
                               @Query("from") int from,
                               @Query("to") int to);

    @GET("maxOrder")
    Observable<MaxOrder> getMaxOrder(@Query("key") String key,
                                     @Query("orderFBID") String orderFBID);





    //POST

    @POST("user")
    @FormUrlEncoded
    Observable<UpdateUserModel> postUser(@Field("key") String key,
                                         @Field("fbid") String fbid,
                                         @Field("userPhone") String phone,
                                         @Field("userName") String name,
                                         @Field("userAddress") String address);

    @POST("favorite")
    @FormUrlEncoded
    Observable<PostFavorite> insertFavorite(@Field("key") String key,
                                            @Field("fbid") String fbid,
                                            @Field("foodId") int foodId,
                                            @Field("restaurantId") int restaurantId,
                                            @Field("restaurantName") String restaurantName,
                                            @Field("foodName") String foodName,
                                            @Field("foodImage") String foodImage,
                                            @Field("price") double price);

    @POST("createOrder")
    @FormUrlEncoded
    Observable<CreateOrder> createOrder(@Field("key") String key,
                                        @Field("orderFBID") String orderFBID,
                                        @Field("orderPhone") String orderPhone,
                                        @Field("orderName") String orderName,
                                        @Field("orderAddress") String orderAddress,
                                        @Field("orderDate") String orderDate,
                                        @Field("restaurantId") int restaurantId,
                                        @Field("transactionId") String transactionId,
                                        @Field("cod") boolean cod,
                                        @Field("totalPrice") double totalPrice,
                                        @Field("numOfItem") int numOfItem);

    @POST("updateOrder")
    @FormUrlEncoded
    Observable<UpdateOrder> updateOrder(@Field("key") String key,
                                        @Field("orderId") String orderId,
                                        @Field("orderDetail") String foodId);

    //DELETE
    @DELETE("favorite")
    Observable<Favorite> removeFavorite(@Query("key") String key,
                                        @Query("fbid") String fbid,
                                        @Query("foodId") int foodId,
                                        @Query("restaurantId") int restaurantId);




}
