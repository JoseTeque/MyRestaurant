package com.hermosaprogramacion.premium.androidmyrestaurant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.Toast;


import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hermosaprogramacion.premium.androidmyrestaurant.adapter.MyCategoryAdapter;
import com.hermosaprogramacion.premium.androidmyrestaurant.common.Common;
import com.hermosaprogramacion.premium.androidmyrestaurant.common.SpaceItemDecoration;
import com.hermosaprogramacion.premium.androidmyrestaurant.database.CartDataSource;
import com.hermosaprogramacion.premium.androidmyrestaurant.database.CartDatabase;
import com.hermosaprogramacion.premium.androidmyrestaurant.database.LocalCartDataSource;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus.MenuItemEvent;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.IMyRestaurantAPI;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.RetrofitClient;
import com.nex3z.notificationbadge.NotificationBadge;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MenuActivity extends AppCompatActivity {

    @BindView(R.id.img_restaurante)
    KenBurnsView img_restaurante;

    @BindView(R.id.recycler_category)
    RecyclerView recycler_category;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.badge)
    NotificationBadge badge;

    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;

    MyCategoryAdapter adapter;

    CartDataSource cartDataSource;

    LayoutAnimationController animationController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        init();

        initView();

        countCartByRestaurant();

        loadFavoriteByRestaurant();

    }

    private void loadFavoriteByRestaurant() {
        compositeDisposable.add(myRestaurantAPI.getFavoriteByRestaurant(Common.API_KEY, Common.currentUser.getFbid(), Common.currentRestaurant.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(favoriteOnLyIdModel -> {
                    if (favoriteOnLyIdModel.isSucces()) {
                        if (favoriteOnLyIdModel.getResult() != null && favoriteOnLyIdModel.getResult().size() > 0) {
                            Common.currentFavOfRestaurant = favoriteOnLyIdModel.getResult();
                        } else {
                            Common.currentFavOfRestaurant = new ArrayList<>();
                        }
                    } else {
                        Toast.makeText(this, "[GET FAVORITE Result]" + favoriteOnLyIdModel.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    Toast.makeText(this, "[GET FAVORITE]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                })
        );
    }

    private void countCartByRestaurant() {
        cartDataSource.countItemInCart(Common.currentUser.getFbid(), Common.currentRestaurant.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        badge.setText(String.valueOf(integer));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MenuActivity.this, "[COUNT CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDao());
    }

    private void initView() {
        ButterKnife.bind(this);

        animationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_item_from_left);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startActivity(new Intent(MenuActivity.this,CarListActivity.class));
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter != null) {
                    switch (adapter.getItemViewType(position)) {
                        case Common.DEFAULT_COLUM_COUNT:
                            return 1;
                        case Common.FULL_WIDTH_COLUM:
                            return 2;
                        default:
                            return -1;
                    }
                } else
                    return -1;
            }
        });

        recycler_category.setLayoutManager(layoutManager);
        recycler_category.addItemDecoration(new SpaceItemDecoration(8));
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    //Event BUS


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
    public void loadMenuByRestaurant(MenuItemEvent event) {
        if (event.isSucces()) {
            Picasso.get().load(event.getRestaurantItem().getImage()).into(img_restaurante);
            toolbar.setTitle(event.getRestaurantItem().getName());
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            //request category by restaurant Id

            compositeDisposable.add(myRestaurantAPI.getMenu(Common.API_KEY,
                    event.getRestaurantItem().getId()).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(menu -> {
                        adapter = new MyCategoryAdapter(this, menu.getResult());
                        recycler_category.setAdapter(adapter);
                        recycler_category.setLayoutAnimation(animationController);

                    }, throwable -> {
                        Toast.makeText(this, "[GET CATEGORY]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        countCartByRestaurant();
    }
}
