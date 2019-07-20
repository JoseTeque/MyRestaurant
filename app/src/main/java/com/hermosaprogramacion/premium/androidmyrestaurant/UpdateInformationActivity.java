package com.hermosaprogramacion.premium.androidmyrestaurant;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.google.android.material.textfield.TextInputEditText;
import com.hermosaprogramacion.premium.androidmyrestaurant.common.Common;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.IMyRestaurantAPI;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.RetrofitClient;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class UpdateInformationActivity extends AppCompatActivity {

    @BindView(R.id.edtx_user_name)
    EditText edtxName;

    @BindView(R.id.edtx_user_address)
    EditText edtxAddress;

    @BindView(R.id.btn_update)
    Button btnUpdate;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    AlertDialog dialog;
    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_information);

        ButterKnife.bind(this);

        init();
        initView();
    }

    private void initView() {
        toolbar.setTitle(getString(R.string.update_information));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnUpdate.setOnClickListener(v -> {
            dialog.show();

            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(Account account) {

                    compositeDisposable.add(
                            myRestaurantAPI.postUser(Common.API_KEY,account.getId(),
                           account.getPhoneNumber().toString(), edtxName.getText().toString(),edtxAddress.getText().toString())
                            .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(updateUserModel -> {

                            if (updateUserModel.isSuccess())
                            {
                              //if user has been update, just refresh again
                                compositeDisposable.add(myRestaurantAPI.getUser(Common.API_KEY, account.getId())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(userModel -> {

                                            if (userModel.isSuccess())
                                            {
                                                Common.currentUser = userModel.getResult().get(0);
                                                startActivity(new Intent(UpdateInformationActivity.this, HomeActivity.class));
                                                finish();
                                            }
                                            else
                                            {
                                                Toast.makeText(UpdateInformationActivity.this, "[GET USER RESULT]" + userModel.getMessage(), Toast.LENGTH_SHORT).show();
                                            }

                                           dialog.dismiss();

                                        },throwable -> {
                                            dialog.dismiss();
                                            Toast.makeText(UpdateInformationActivity.this, "[ GET USER ]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        }));

                            }else
                            {
                                dialog.dismiss();
                                Toast.makeText(UpdateInformationActivity.this, "[ UPDATE USER API RETURN ]" + updateUserModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                    },throwable -> {
                        dialog.dismiss();
                        Toast.makeText(UpdateInformationActivity.this, "[UPDATE USER API]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }));
                }

                @Override
                public void onError(AccountKitError accountKitError) {
                    Toast.makeText(UpdateInformationActivity.this, "[Account kit error]" + accountKitError.getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        if (Common.currentUser != null && !TextUtils.isEmpty(Common.currentUser.getName()))
            edtxName.setText(Common.currentUser.getName());
        if (Common.currentUser != null && !TextUtils.isEmpty(Common.currentUser.getAddress()))
            edtxAddress.setText(Common.currentUser.getAddress());
    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        myRestaurantAPI= RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();
        if (id == android.R.id.home){
            finish(); //close this activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
