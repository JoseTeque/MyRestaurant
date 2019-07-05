package com.hermosaprogramacion.premium.androidmyrestaurant;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.accountkit.AccountKit;

import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.hermosaprogramacion.premium.androidmyrestaurant.adapter.BannerSliderAdapter;
import com.hermosaprogramacion.premium.androidmyrestaurant.adapter.MyRestaurantAdapter;
import com.hermosaprogramacion.premium.androidmyrestaurant.common.Common;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.RestaurantItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus.RestaurantLoadEvent;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.IMyRestaurantAPI;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.RetrofitClient;
import com.hermosaprogramacion.premium.androidmyrestaurant.service.PicassoImageLoadingService;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ss.com.bannerslider.Slider;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    TextView txt_name, txt_phone;

    @BindView(R.id.banner_slider)
    Slider banner_slider;

    @BindView(R.id.recycler_restaurant)
    RecyclerView recyclerView_restaurant;

    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        txt_name = headerView.findViewById(R.id.txt_user_name);
        txt_phone = headerView.findViewById(R.id.txt_user_phone);

        txt_name.setText(Common.currentUser.getName());
        txt_phone.setText(Common.currentUser.getUserPhone());

        init();
        initView();
        loadRestaurant();

    }

    private void loadRestaurant() {
        dialog.show();

        compositeDisposable.add(myRestaurantAPI.getRestaurant(Common.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(restaurant -> {
                    // Here, we will use Eventbus to send local event sent adapter and slider
                    EventBus.getDefault().post(new RestaurantLoadEvent( true, restaurant.getResult()));

                }, throwable -> {
                    EventBus.getDefault().post(new RestaurantLoadEvent( false, throwable.getMessage()));
                })
        );
    }

    private void initView() {
        ButterKnife.bind(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView_restaurant.setLayoutManager(layoutManager);
        recyclerView_restaurant.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);

        Slider.init(new PicassoImageLoadingService());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_log_out) {
            // Handle the camera action
            signOut();
        } else if (id == R.id.nav_nearby) {

            startActivity(new Intent(HomeActivity.this, NearbyRestaurantActivity.class));

        } else if (id == R.id.nav_order_history) {

            startActivity(new Intent(HomeActivity.this, ViewOrderActivity.class));
            finish();

        } else if (id == R.id.nav_update_info) {

            startActivity(new Intent(HomeActivity.this, UpdateInformationActivity.class));

        }else if (id == R.id.nav_favo) {
           startActivity(new Intent(this, FavoriteActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {

        AlertDialog confirmDialog = new AlertDialog.Builder(this)
                .setTitle("Sign out")
                .setMessage("Do you really want to sign out")
                .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("OK", (dialog, which) -> {
                    Common.currentUser = null;
                    Common.currentRestaurant = null;

                    AccountKit.logOut();
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }).create();
        confirmDialog.show();

    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    /*
    REGISTER EVENT BUS
     */

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

    //LISTEN EVENT BUS

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void proccesRestaurantLoadEvent(RestaurantLoadEvent event)
    {
        if (event.isSucces())
        {
          displayBanner(event.getRestaurantList());
          displayRestaurant(event.getRestaurantList());

        }else
        {
            Toast.makeText(this, "[RESTAURANT LOAD]" + event.getMessage(), Toast.LENGTH_SHORT).show();
        }
        dialog.dismiss();
    }

    private void displayRestaurant(List<RestaurantItem> restaurantList) {
        MyRestaurantAdapter adapter = new MyRestaurantAdapter(this, restaurantList);
        recyclerView_restaurant.setAdapter(adapter);
    }

    private void displayBanner(List<RestaurantItem> restaurantList) {
        banner_slider.setAdapter(new BannerSliderAdapter(restaurantList));
    }


}
