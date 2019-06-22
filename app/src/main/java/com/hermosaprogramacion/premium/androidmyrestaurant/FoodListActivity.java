package com.hermosaprogramacion.premium.androidmyrestaurant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hermosaprogramacion.premium.androidmyrestaurant.adapter.MyCategoryAdapter;
import com.hermosaprogramacion.premium.androidmyrestaurant.adapter.MyFoodListAdapter;
import com.hermosaprogramacion.premium.androidmyrestaurant.common.Common;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus.FoodListEvent;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.IMyRestaurantAPI;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.RetrofitClient;
import com.nex3z.notificationbadge.NotificationBadge;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FoodListActivity extends AppCompatActivity {

    @BindView(R.id.img_categoria)
    ImageView img_categoria;

    @BindView(R.id.recycler_food)
    RecyclerView recycler_food;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        init();
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_food.setLayoutManager(layoutManager);
        recycler_food.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));

    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

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
    public void loadFoodByMenu(FoodListEvent event) {
        dialog.show();
        if (event.isSucces()) {
            Picasso.get().load(event.getMenuItem().getImage()).into(img_categoria);
            toolbar.setTitle(event.getMenuItem().getName());

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            compositeDisposable.add(myRestaurantAPI.getFood(Common.API_KEY,
                    event.getMenuItem().getId()).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(food -> {

                        if (food.isSucces())
                        {
                            MyFoodListAdapter adapter = new MyFoodListAdapter(this, food.getResult());
                            recycler_food.setAdapter(adapter);

                        }else {
                            Toast.makeText(this, "[GET FOOD RESULT]" + food.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();

                    }, throwable -> {
                        dialog.dismiss();
                        Toast.makeText(this, "[GET FOOD]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    })

            );
        }
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
}
