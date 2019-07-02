package com.hermosaprogramacion.premium.androidmyrestaurant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.hermosaprogramacion.premium.androidmyrestaurant.adapter.MyAddonAdapter;
import com.hermosaprogramacion.premium.androidmyrestaurant.common.Common;
import com.hermosaprogramacion.premium.androidmyrestaurant.database.CartDataSource;
import com.hermosaprogramacion.premium.androidmyrestaurant.database.CartDatabase;
import com.hermosaprogramacion.premium.androidmyrestaurant.database.CartItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.database.LocalCartDataSource;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.Food;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.FoodItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.SizeItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus.AddonEventChange;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus.AddonLoadEvent;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus.FoodDetailEvent;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus.SizeLocalEvent;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.IMyRestaurantAPI;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.RetrofitClient;
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

public class FoodDetailActivity extends AppCompatActivity {

    @BindView(R.id.fab_to_cart)
    FloatingActionButton fab_to_cart;

    @BindView(R.id.btn_view_cart)
    Button btn_view_cart;

    @BindView(R.id.txt_money)
    TextView txt_money;

    @BindView(R.id.rdi_group_size)
    RadioGroup rdi_group_size;

    @BindView(R.id.recycler_Addon)
    RecyclerView recycler_Addon;

    @BindView(R.id.txt_description)
    TextView txt_description;

    @BindView(R.id.img_categoriaDE)
    ImageView img_categoria;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    FoodItem selectedFood;

    double originalPrice;

    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;
    CartDataSource cartDataSource;
    private double sizeprice = 0.0;
    private String selectedSize;
    private double addonPrice = 0.0;
    private double extraPrice;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        init();
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);

        fab_to_cart.setOnClickListener(v -> {

            //create cart
            CartItem cartItem = new CartItem();
            cartItem.setFoodId(selectedFood.getId());
            cartItem.setFoodName(selectedFood.getName());
            cartItem.setFoodImage(selectedFood.getImage());
            cartItem.setFoodPrice(selectedFood.getPrice());
            cartItem.setFoodQuantity(1);
            cartItem.setUserPhone(Common.currentUser.getUserPhone());
            cartItem.setRestaurantId(Common.currentRestaurant.getId());
            cartItem.setFoodAddon(new Gson().toJson(Common.addonList));
            cartItem.setFoodSize(selectedSize);
            cartItem.setFoodExtraPrice(extraPrice);
            cartItem.setFbid(Common.currentUser.getFbid());

            compositeDisposable.add(
                    cartDataSource.inserOrReplaceAll(cartItem)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(()-> {

                                Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();


                            }, throwable -> {
                                Toast.makeText(this, "[ADD CART]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            })
            );

        });
    }

    private void init() {
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDao());
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);

    }

    // Star Event


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
    public void displayFoodDetail(FoodDetailEvent event) {
        if (event.isSucces()) {

            toolbar.setTitle(event.getFood().getName());

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            selectedFood = event.getFood();
            originalPrice = event.getFood().getPrice();

            txt_money.setText(String.valueOf(originalPrice));
            txt_description.setText(selectedFood.getDescription());
            Picasso.get().load(event.getFood().getImage()).into(img_categoria);

            if (event.getFood().getIsSize() && event.getFood().getIsAddon()) {
                //load size and addon from server
                dialog.show();

                compositeDisposable.add(myRestaurantAPI.getSize(Common.API_KEY, event.getFood().getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(size -> {

                            //send local event bus
                            EventBus.getDefault().post(new SizeLocalEvent(true, size.getResult()));

                            dialog.show();
                            // load Addon
                            compositeDisposable.add(myRestaurantAPI.getAddon(Common.API_KEY, event.getFood().getId())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(addon -> {

                                        if (addon.isSucces()) {
                                            EventBus.getDefault().post(new AddonLoadEvent(true, addon.getResult()));
                                        } else {
                                            Toast.makeText(this, "[LOAD ADDON RESULT]" + addon.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                        dialog.dismiss();
                                    }, throwable -> {
                                        dialog.dismiss();
                                        Toast.makeText(this, "[LOAD ADDON]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    })
                            );

                            dialog.dismiss();
                        }, throwable -> {

                            dialog.dismiss();
                            Toast.makeText(this, "[LOAD SIZE]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        })
                );
            } else {
                if (event.getFood().getIsSize()) {
                    compositeDisposable.add(myRestaurantAPI.getSize(Common.API_KEY, event.getFood().getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(size -> {
                                if (size.isSucces()) {
                                    //send local event bus
                                    EventBus.getDefault().post(new SizeLocalEvent(true, size.getResult()));


                                } else {
                                    Toast.makeText(this, "[SIZE LOAD RESULT]" + size.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                dialog.dismiss();
                            }, throwable -> {
                                dialog.dismiss();
                                Toast.makeText(this, "[LOAD SIZE]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            })
                    );

                }
                if (event.getFood().getIsAddon()) {
                    dialog.show();
                    // load Addon
                    compositeDisposable.add(myRestaurantAPI.getAddon(Common.API_KEY, event.getFood().getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(addon -> {

                                if (addon.isSucces()) {
                                    EventBus.getDefault().post(new AddonLoadEvent(true, addon.getResult()));
                                } else {
                                    Toast.makeText(this, "[LOAD ADDON RESULT]" + addon.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }, throwable -> {
                                dialog.dismiss();
                                Toast.makeText(this, "[LOAD ADDON]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            })
                    );
                }
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displaySize(SizeLocalEvent event) {
        if (event.isSucces()) {
            //create radio button base on size length
            for (SizeItem sizeItem : event.getSizeList()) {

                RadioButton radioButton = new RadioButton(this);
                radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {

                    if (isChecked)
                        sizeprice = sizeItem.getExtraPrice();
                    else
                        sizeprice = -sizeItem.getExtraPrice();

                    calculatePrice();

                    selectedSize = sizeItem.getDescription();

                });

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                radioButton.setLayoutParams(params);
                radioButton.setText(sizeItem.getDescription());
                radioButton.setTag(sizeItem.getExtraPrice());

                rdi_group_size.addView(radioButton);
            }
        }
    }

    private void calculatePrice() {

         extraPrice = 0.0;
        double newPrice ;

        extraPrice  += sizeprice;
        extraPrice  += addonPrice;

            newPrice = originalPrice += extraPrice;

        txt_money.setText("");

        txt_money.setText(String.valueOf(newPrice));

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displayAddon(AddonLoadEvent event) {
        if (event.isSucces()) {
            recycler_Addon.setHasFixedSize(true);
            recycler_Addon.setLayoutManager(new LinearLayoutManager(this));
            recycler_Addon.setAdapter(new MyAddonAdapter(this, event.getAddonList()));

        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void priceChange(AddonEventChange eventChange) {
        if (eventChange.isAddon())
            addonPrice += eventChange.getAddonItem().getExtraPrice();
        else
            addonPrice -= eventChange.getAddonItem().getExtraPrice();

        calculatePrice();
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
