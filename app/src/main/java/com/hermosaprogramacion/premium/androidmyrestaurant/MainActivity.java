package com.hermosaprogramacion.premium.androidmyrestaurant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.hermosaprogramacion.premium.androidmyrestaurant.common.Common;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.IMyRestaurantAPI;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.RetrofitClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;

    private static final int APP_REQUEST_CODE = 1234;

    @BindView(R.id.btn_sign_in)
    Button btnLogin;

    @OnClick(R.id.btn_sign_in)
    void loginUser() {
        Intent intent = new Intent(MainActivity.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE, AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(MainActivity.this);

        init();
    }

    private void init() {

        Paper.init(this);
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == APP_REQUEST_CODE) {
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toasMessage;

            if (loginResult.getError() != null) {
                toasMessage = loginResult.getError().getErrorType().getMessage();
                Toast.makeText(this, " " + toasMessage, Toast.LENGTH_SHORT).show();
            } else if (loginResult.wasCancelled()) {
                Toast.makeText(this, "Cancel Login Facebook Account ", Toast.LENGTH_SHORT).show();
            } else {
                dialog.show();

                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(Account account) {

                        Paper.book().write(Common.REMEMBER_FBID,account.getId());

                        FirebaseInstanceId.getInstance()
                                .getInstanceId()
                                .addOnFailureListener(e -> {
                                    Toast.makeText(MainActivity.this, "[GET TOKEN]" +e.getMessage(), Toast.LENGTH_SHORT).show();
                                }).addOnCompleteListener(task -> {

                                  compositeDisposable.add(myRestaurantAPI.postToken(Common.API_KEY,
                                          account.getId(),task.getResult().getToken())
                                  .subscribeOn(Schedulers.io())
                                  .observeOn(AndroidSchedulers.mainThread())
                                  .subscribe(token -> {

                                      if (!token.isSuccess())
                                          Toast.makeText(MainActivity.this, "[UPDATE TOKEN]" +token.getMessage(), Toast.LENGTH_SHORT).show();

                                      compositeDisposable.add(myRestaurantAPI.getUser(Common.API_KEY, account.getId())
                                              .subscribeOn(Schedulers.io())
                                              .observeOn(AndroidSchedulers.mainThread())
                                              .subscribe(userModel -> {

                                                  //if user already in database
                                                  if (userModel.isSuccess()) {
                                                      Common.currentUser = userModel.getResult().get(0);
                                                      startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                                      finish();
                                                  } else { //if user not register
                                                      startActivity(new Intent(MainActivity.this,UpdateInformationActivity.class));
                                                      finish();
                                                  }
                                                  dialog.dismiss();
                                              }, throwable -> {
                                                  Toast.makeText(MainActivity.this, "[GET USER]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                              })
                                      );

                                  },throwable -> {
                                      Toast.makeText(MainActivity.this, "[UPDATE TOKEN]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                  }));
                                });
                    }

                    @Override
                    public void onError(AccountKitError accountKitError) {
                        Toast.makeText(MainActivity.this, "[Account kit Error]" + accountKitError.getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
