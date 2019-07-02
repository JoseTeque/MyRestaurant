package com.hermosaprogramacion.premium.androidmyrestaurant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hermosaprogramacion.premium.androidmyrestaurant.common.Common;
import com.hermosaprogramacion.premium.androidmyrestaurant.database.CartDataSource;
import com.hermosaprogramacion.premium.androidmyrestaurant.database.CartDatabase;
import com.hermosaprogramacion.premium.androidmyrestaurant.database.LocalCartDataSource;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.IMyRestaurantAPI;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.RetrofitClient;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.disposables.CompositeDisposable;

public class PlaceOlderActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

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
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;
    CartDataSource cartDataSource;
    boolean isSelectedDate= false, isAddNewAddress = false;



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

            EditText edtx_new_address = add_new_address.findViewById(R.id.edtx_Add_new_Address);
            edtx_new_address.setText(txt_new_Address.getText());

            androidx.appcompat.app.AlertDialog.Builder builder= new androidx.appcompat.app.AlertDialog.Builder(PlaceOlderActivity.this)
                    .setTitle("Add New Address")
                    .setView(edtx_new_address)
                    .setNegativeButton("CANCEL", (dialog, which) -> {
                      dialog.dismiss();
                    }).setPositiveButton("ADD", (dialog, which) -> {
                       txt_new_Address.setText(edtx_new_address.getText());
                    });

            androidx.appcompat.app.AlertDialog addNewAddresDialog = builder.create();
            addNewAddresDialog.show();

        });

        edtx_date.setOnClickListener(v -> {

            Calendar now = Calendar.getInstance();
            DatePickerDialog datePickerDialog= DatePickerDialog.newInstance(PlaceOlderActivity.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
                    );

            datePickerDialog.show(getSupportFragmentManager(),"DatePickerDialog");

        });

        btn_procced.setOnClickListener(v -> {
         if (!isSelectedDate)
         {
             Toast.makeText(this, "Please select date..!", Toast.LENGTH_SHORT).show();
             return;
         }
         if (!isAddNewAddress)
         {
          if (chk_default_address.isChecked())
          {
              Toast.makeText(this, "Seleccione una nueva direccion", Toast.LENGTH_SHORT).show();
          }
         }
         if (rdi_cash_delivery.isChecked())
         {

         }else  if (rdi_online_payment.isChecked())
         {

         }

        });

    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
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
       .append(monthOfYear)
       .append("/")
       .append(dayOfMonth)
       .append("/")
       .append(year));

    }
}
