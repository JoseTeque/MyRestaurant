package com.hermosaprogramacion.premium.androidmyrestaurant.retrofit;

import com.hermosaprogramacion.premium.androidmyrestaurant.model.BraintreeToken;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.BraintreeTransaction;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface IBraintreeAPI {

    @GET("checkouts/new")
    Observable<BraintreeToken> getToken();

    @POST("checkouts")
    @FormUrlEncoded
    Observable<BraintreeTransaction> submitPayment(@Field("amount") String amount, @Field("payment_method_nonce") String nonce );
}
