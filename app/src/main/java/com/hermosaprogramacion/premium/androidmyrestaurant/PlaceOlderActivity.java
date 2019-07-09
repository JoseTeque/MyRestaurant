package com.hermosaprogramacion.premium.androidmyrestaurant;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.gson.Gson;
import com.hermosaprogramacion.premium.androidmyrestaurant.common.Common;
import com.hermosaprogramacion.premium.androidmyrestaurant.database.CartDataSource;
import com.hermosaprogramacion.premium.androidmyrestaurant.database.CartDatabase;
import com.hermosaprogramacion.premium.androidmyrestaurant.database.CartItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.database.LocalCartDataSource;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus.SendTotalCashEvent;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.IBraintreeAPI;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.IMyRestaurantAPI;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.RetrofitBraintreeClient;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.RetrofitClient;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class PlaceOlderActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final int REQUEST_BRAINTREE_CODE = 7777;
    @BindView(R.id.edtx_date)
    EditText edtx_date;

    @BindView(R.id.txt_total_cash)
    TextView txt_total_cash;

    @BindView(R.id.txt_user_phone)
    TextView txt_user_phone;

    @BindView(R.id.txt_new_Address)
    TextView txt_new_Address;

    @BindView(R.id.txt_user_address)
    TextView txt_user_address;

    @BindView(R.id.chk_default_address)
    CheckBox chk_default_address;

    @BindView(R.id.btn_new_Address)
    Button btn_new_Address;

    @BindView(R.id.btn_procced)
    Button btn_procced;

    @BindView(R.id.rdi_cash_delivery)
    RadioButton rdi_cash_delivery;

    @BindView(R.id.rdi_online_payment)
    RadioButton rdi_online_payment;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    IMyRestaurantAPI myRestaurantAPI;
    IBraintreeAPI braintreeAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;
    CartDataSource cartDataSource;
    boolean isSelectedDate = false, isAddNewAddress = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_older);

        init();
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);

        txt_user_phone.setText(Common.currentUser.getUserPhone());
        txt_user_address.setText(Common.currentUser.getAddress());

        toolbar.setTitle(getString(R.string.place_older));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_new_Address.setOnClickListener(v -> {

            isAddNewAddress = true;
            chk_default_address.setChecked(false);

            View add_new_address = LayoutInflater.from(PlaceOlderActivity.this)
                    .inflate(R.layout.add_new_address_layout, null);

            EditText edtx_new_address = (EditText) add_new_address.findViewById(R.id.edtx_Add_new_Address);
            edtx_new_address.setText(txt_new_Address.getText().toString());

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(PlaceOlderActivity.this)
                    .setTitle("Add New Address")
                    .setView(add_new_address)
                    .setNegativeButton("CANCEL", (dialog, which) -> {
                        dialog.dismiss();
                    }).setPositiveButton("ADD", (dialog, which) -> {
                        txt_new_Address.setText(edtx_new_address.getText().toString());
                    });

            androidx.appcompat.app.AlertDialog addNewAddresDialog = builder.create();

            addNewAddresDialog.show();

        });

        edtx_date.setOnClickListener(v -> {

            Calendar now = Calendar.getInstance();
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(PlaceOlderActivity.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.show(getSupportFragmentManager(), "DatePickerDialog");

        });

        btn_procced.setOnClickListener(v -> {
            if (!isSelectedDate) {
                Toast.makeText(this, "Please select date..!", Toast.LENGTH_SHORT).show();
                return;
            } else {
                String dateString = edtx_date.getText().toString();
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                try {
                    Date orderDate = df.parse(dateString);

                    //GET CURRENT DATE
                    Calendar calendar = Calendar.getInstance();
                    Date currenDate = df.parse(df.format(calendar.getTime()));

                    if (!DateUtils.isToday(orderDate.getTime()))
                    {
                        if (orderDate.before(currenDate)) {
                            Toast.makeText(this, "Please choose current date or  future day", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }


                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (!isAddNewAddress) {
                if (!chk_default_address.isChecked()) {
                    Toast.makeText(this, "Seleccione una nueva direccion", Toast.LENGTH_SHORT).show();
                }
            }
            if (rdi_cash_delivery.isChecked()) {
                getOrderNumber(false);

            } else if (rdi_online_payment.isChecked()) {
                getOrderNumber(true);
            }

        });

    }

    private void getOrderNumber(boolean isOnlinePayment) {
        dialog.show();
        if (!isOnlinePayment) {
            String address = chk_default_address.isChecked() ? txt_user_address.getText().toString() : txt_new_Address.getText().toString();

            compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getFbid(), Common.currentRestaurant.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(cartItems -> {

                        //get order number from server
                        compositeDisposable.add(
                                myRestaurantAPI.createOrder(
                                        Common.API_KEY,
                                        Common.currentUser.getFbid(),
                                        Common.currentUser.getUserPhone(),
                                        Common.currentUser.getName(),
                                        address,
                                        edtx_date.getText().toString(),
                                        Common.currentRestaurant.getId(),
                                        "NONE",
                                        true,
                                        Double.valueOf(txt_total_cash.getText().toString()),
                                        cartItems.size())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(createOrder -> {

                                            if (createOrder.isSucces()) {
                                                // after have order number, we will update all item of this order to orderDetail
                                                // first, select cart items
                                                compositeDisposable.add(myRestaurantAPI.updateOrder(Common.API_KEY, String.valueOf(createOrder.getResult().get(0).getOrderNumber()),
                                                        new Gson().toJson(cartItems))
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(updateOrder -> {

                                                            if (updateOrder.isSucces()) {
                                                                //after update item, we will clear cart and show message success
                                                                cartDataSource.cleanCart(Common.currentUser.getFbid(), Common.currentRestaurant.getId())
                                                                        .subscribeOn(Schedulers.io())
                                                                        .observeOn(AndroidSchedulers.mainThread())
                                                                        .subscribe(new SingleObserver<Integer>() {
                                                                            @Override
                                                                            public void onSubscribe(Disposable d) {

                                                                            }

                                                                            @Override
                                                                            public void onSuccess(Integer integer) {

                                                                                Toast.makeText(PlaceOlderActivity.this, "Order Placed", Toast.LENGTH_SHORT).show();
                                                                                Intent homeIntent = new Intent(PlaceOlderActivity.this, HomeActivity.class);
                                                                                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                startActivity(homeIntent);
                                                                                finish();
                                                                            }

                                                                            @Override
                                                                            public void onError(Throwable e) {
                                                                                Toast.makeText(PlaceOlderActivity.this, "[CLEAN CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            }

                                                            if (dialog.isShowing())
                                                                dialog.dismiss();


                                                        }, throwable -> {
                                                            dialog.dismiss();
                                                            // Toast.makeText(this, "[UPDATE ORDER]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                        })
                                                );
                                            } else {
                                                dialog.dismiss();
                                                Toast.makeText(this, "[CREATE ORDER]" + createOrder.getMessage(), Toast.LENGTH_SHORT).show();
                                            }

                                        }, throwable -> {
                                            dialog.dismiss();
                                            Toast.makeText(this, "[CREATE ORDER]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        }));

                    }, throwable -> {
                        Toast.makeText(this, "[GET ALL CART]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    })
            );
        } else {
            //if online payment
            //first, get token
            compositeDisposable.add(braintreeAPI.getToken()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(braintreeToken -> {
                        if (braintreeToken.isSuccess()) {
                            DropInRequest dropInRequest = new DropInRequest().clientToken(braintreeToken.getClientToken());
                            startActivityForResult(dropInRequest.getIntent(this), REQUEST_BRAINTREE_CODE);
                        } else {
                            Toast.makeText(this, "[Cannot get token", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();

                    }, throwable -> {
                        dialog.dismiss();
                        Toast.makeText(this, "[GET TOKEN]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }));
        }
    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
        braintreeAPI = RetrofitBraintreeClient.getInstance(Common.currentRestaurant.getPaymentUrl()).create(IBraintreeAPI.class);
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDao());
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        isSelectedDate = true;

        edtx_date.setText(new StringBuilder("")
                .append(monthOfYear + 1)
                .append("/")
                .append(dayOfMonth)
                .append("/")
                .append(year));

    }

    //Event bus


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void setTotalCash(SendTotalCashEvent event) {
        txt_total_cash.setText(String.valueOf(event.getCash()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_BRAINTREE_CODE) {
            if (resultCode == RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();

                //after, have nonce, we just made a payment with API
                if (!TextUtils.isEmpty(txt_total_cash.getText().toString())) {
                    String amount = txt_total_cash.getText().toString();

                    if (!dialog.isShowing())
                        dialog.show();

                    String address = chk_default_address.isChecked() ? txt_user_address.getText().toString() : txt_new_Address.getText().toString();

                    compositeDisposable.add(braintreeAPI.submitPayment(amount, nonce.getNonce())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(braintreeTransaction -> {

                                if (braintreeTransaction.isSuccess()) {
                                    if (!dialog.isShowing())
                                        dialog.show();

                                    //after we have transaction, just make order like cod payment

                                    compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getFbid(), Common.currentRestaurant.getId())
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(cartItems -> {

                                                //get order number from server
                                                compositeDisposable.add(
                                                        myRestaurantAPI.createOrder(
                                                                Common.API_KEY,
                                                                Common.currentUser.getFbid(),
                                                                Common.currentUser.getUserPhone(),
                                                                Common.currentUser.getName(),
                                                                address,
                                                                edtx_date.getText().toString(),
                                                                Common.currentRestaurant.getId(),
                                                                braintreeTransaction.getTransaction().getId(),
                                                                false,
                                                                Double.valueOf(amount),
                                                                cartItems.size())
                                                                .subscribeOn(Schedulers.io())
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .subscribe(createOrder -> {

                                                                    if (createOrder.isSucces()) {
                                                                        // after have order number, we will update all item of this order to orderDetail
                                                                        // first, select cart items
                                                                        compositeDisposable.add(myRestaurantAPI.updateOrder(Common.API_KEY, String.valueOf(createOrder.getResult().get(0).getOrderNumber()),
                                                                                new Gson().toJson(cartItems))
                                                                                .subscribeOn(Schedulers.io())
                                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                                .subscribe(updateOrder -> {

                                                                                    if (updateOrder.isSucces()) {
                                                                                        //after update item, we will clear cart and show message success
                                                                                        cartDataSource.cleanCart(Common.currentUser.getFbid(), Common.currentRestaurant.getId())
                                                                                                .subscribeOn(Schedulers.io())
                                                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                                                .subscribe(new SingleObserver<Integer>() {
                                                                                                    @Override
                                                                                                    public void onSubscribe(Disposable d) {

                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onSuccess(Integer integer) {

                                                                                                        Toast.makeText(PlaceOlderActivity.this, "Order Placed", Toast.LENGTH_SHORT).show();
                                                                                                        Intent homeIntent = new Intent(PlaceOlderActivity.this, HomeActivity.class);
                                                                                                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                                        startActivity(homeIntent);
                                                                                                        finish();
                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onError(Throwable e) {
                                                                                                        Toast.makeText(PlaceOlderActivity.this, "[CLEAN CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                });
                                                                                    }

                                                                                    if (dialog.isShowing())
                                                                                        dialog.dismiss();


                                                                                }, throwable -> {
                                                                                    dialog.dismiss();
                                                                                    // Toast.makeText(this, "[UPDATE ORDER]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                })
                                                                        );
                                                                    } else {
                                                                        dialog.dismiss();
                                                                        Toast.makeText(this, "[CREATE ORDER]" + createOrder.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }

                                                                }, throwable -> {
                                                                    dialog.dismiss();
                                                                    Toast.makeText(this, "[CREATE ORDER]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }));

                                            }, throwable -> {
                                                Toast.makeText(this, "[GET ALL CART]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                            })
                                    );
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(this, "[Transaction failed..!]", Toast.LENGTH_SHORT).show();
                                }

                            }, throwable -> {
                                if (dialog.isShowing())
                                    dialog.dismiss();
                                Toast.makeText(this, "[SUBMIT PAYMENT]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();

                            }));
                }
            }
        }
    }
}
