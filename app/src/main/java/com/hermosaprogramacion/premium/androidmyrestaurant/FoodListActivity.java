package com.hermosaprogramacion.premium.androidmyrestaurant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.Toast;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hermosaprogramacion.premium.androidmyrestaurant.adapter.MyCategoryAdapter;
import com.hermosaprogramacion.premium.androidmyrestaurant.adapter.MyFoodListAdapter;
import com.hermosaprogramacion.premium.androidmyrestaurant.common.Common;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.Food;
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
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FoodListActivity extends AppCompatActivity {

    @BindView(R.id.img_categoria)
    KenBurnsView img_categoria;

    @BindView(R.id.recycler_food)
    RecyclerView recycler_food;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;

    MyFoodListAdapter adapter, searchAdapter;

    LayoutAnimationController animationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        init();
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);

        animationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_item_from_left);

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
        if (adapter !=null)
            adapter.onDestroy();
        if (searchAdapter !=null)
            searchAdapter.onDestroy();
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
                             adapter = new MyFoodListAdapter(this, food.getResult());
                            recycler_food.setAdapter(adapter);
                            recycler_food.setLayoutAnimation(animationController);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.menu_search,menu);

        MenuItem menuItem= menu.findItem(R.id.search);

        SearchManager searchManager=(SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView= (SearchView) menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        //Event
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                starSearchFood(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                //Restore to original adapter when use close search
                recycler_food.setAdapter(adapter);
                return true;
            }
        });
        return true;
    }

    private void starSearchFood(String query) {

        dialog.show();

        compositeDisposable.add(myRestaurantAPI.getSearch(Common.API_KEY, query)
           .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(food -> {

                    if (food.isSucces())
                    {
                      searchAdapter = new MyFoodListAdapter(this, food.getResult() );
                      recycler_food.setAdapter(searchAdapter);

                    }else
                    {
                        if (food.getMessage().contains("Empty"))
                        {
                            recycler_food.setAdapter(null);
                            Toast.makeText(this, "Not found", Toast.LENGTH_SHORT).show();
                        }

                    }
                    dialog.dismiss();

                }, throwable -> {
                    dialog.dismiss();
                    Toast.makeText(this, "[SEARCH FOOD]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();

                })
        );

    }

}
