package com.hermosaprogramacion.premium.androidmyrestaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.hermosaprogramacion.premium.androidmyrestaurant.common.Common;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.IMyRestaurantAPI;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.RetrofitClient;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class Splashctivity extends AppCompatActivity {

    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                            @Override
                            public void onSuccess(Account account) {

                                dialog.show();

                                compositeDisposable.add(myRestaurantAPI.getUser(Common.API_KEY, account.getId())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(userModel -> {

                                                    if (userModel.isSucces()) //if user available in database
                                                    {
                                                        Common.currentUser = userModel.getResult().get(0);
                                                        startActivity(new Intent(Splashctivity.this, HomeActivity.class));
                                                        finish();
                                                    } else {  //if not available user in database, star UpdateInformationActivity for register
                                                        startActivity(new Intent(Splashctivity.this, UpdateInformationActivity.class));
                                                        finish();
                                                    }

                                                    dialog.dismiss();
                                                },
                                                throwable -> {
                                                    dialog.dismiss();
                                                    Toast.makeText(Splashctivity.this, "[GET USER API]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                }));
                            }

                            @Override
                            public void onError(AccountKitError accountKitError) {
                                Toast.makeText(Splashctivity.this, "Not sign in ! please sign in", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Splashctivity.this, MainActivity.class));
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(Splashctivity.this, "You must accept this permission to use our app", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();

        //      printKeyHash();

        //        new Handler().postDelayed(new Runnable() {
        //          @Override
        //        public void run() {
        //        startActivity(new Intent(Splashctivity.this,MainActivity.class));
        //      finish();
        //       }
        //     }, 3000);
    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
    }

   /* private void printKeyHash() {

        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info= getPackageManager().getPackageInfo("joseguerra.ordereat",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature:info.signatures)
            {
                MessageDigest md= MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("keyHash", Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    } */
}
