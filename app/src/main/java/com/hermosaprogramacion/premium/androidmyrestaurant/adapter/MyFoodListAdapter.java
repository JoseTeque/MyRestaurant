package com.hermosaprogramacion.premium.androidmyrestaurant.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hermosaprogramacion.premium.androidmyrestaurant.FoodDetailActivity;
import com.hermosaprogramacion.premium.androidmyrestaurant.R;
import com.hermosaprogramacion.premium.androidmyrestaurant.common.Common;
import com.hermosaprogramacion.premium.androidmyrestaurant.database.CartDataSource;
import com.hermosaprogramacion.premium.androidmyrestaurant.database.CartDatabase;
import com.hermosaprogramacion.premium.androidmyrestaurant.database.CartItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.database.LocalCartDataSource;
import com.hermosaprogramacion.premium.androidmyrestaurant.interfac.IFoodDetailOrCartCliclListener;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.FavoriteOnlyId;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.FoodItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus.FoodDetailEvent;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.IMyRestaurantAPI;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MyFoodListAdapter extends RecyclerView.Adapter<MyFoodListAdapter.ViewHolder> {


    private Context context;
    private List<FoodItem> foodItemList;
   private CompositeDisposable compositeDisposable;
    private CartDataSource cartDataSource;
    private IMyRestaurantAPI myRestaurantAPI;


    public void onDestroy() {
        compositeDisposable.clear();
    }

    public MyFoodListAdapter(Context context, List<FoodItem> foodItemList) {
        this.context = context;
        this.foodItemList = foodItemList;
        compositeDisposable = new CompositeDisposable();
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDao());
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_food_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem item = foodItemList.get(position);

        holder.txt_food_name.setText(item.getName());
        Picasso.get().load(item.getImage()).placeholder(R.drawable.app_icon).into(holder.img_food);
        holder.txt_food_price.setText(new StringBuilder(context.getString(R.string.money_sign)).append(item.getPrice()));

        //Check favorite
        if (Common.currentFavOfRestaurant != null && Common.currentFavOfRestaurant.size() > 0) {

            if (Common.checkFavorite(foodItemList.get(position).getId())) {

                holder.img_favo.setImageResource(R.drawable.ic_favorite_primary_color_24dp);
                holder.img_favo.setTag(true);

            } else {

                holder.img_favo.setImageResource(R.drawable.ic_favorite_border_primary_color_24dp);
                holder.img_favo.setTag(false);
            }
        } else {
            // Default, all item is no favorite
            holder.img_favo.setTag(false);
        }

        //Event
        holder.img_favo.setOnClickListener(view -> {


            ImageView fav= (ImageView)view;

            if ((Boolean)view.getTag()) {
                //if tag = true -> favorite item clicked
                compositeDisposable.add(myRestaurantAPI.removeFavorite(Common.API_KEY,
                        Common.currentUser.getFbid(),foodItemList.get(position).getId(),Common.currentRestaurant.getId())
                .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(favorite -> {

                            if (favorite.isSuccess())
                            {
                                fav.setImageResource(R.drawable.ic_favorite_border_primary_color_24dp);
                                fav.setTag(false);
                                if (Common.currentFavOfRestaurant != null)
                                    Common.removeFavorite(foodItemList.get(position).getId());
                            }
                            else
                            {
                                Toast.makeText(context, "[REMOVE FAVORITE RESULT]" + favorite.getMessage(), Toast.LENGTH_SHORT).show();
                            }


                        },throwable -> {

                           // Toast.makeText(context, "[REMOVE FAVORITE]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        })
                );
            }else
            {
                //if tag = true -> favorite item clicked
                compositeDisposable.add(myRestaurantAPI.insertFavorite(Common.API_KEY,
                        Common.currentUser.getFbid(),
                        foodItemList.get(position).getId(),
                        Common.currentRestaurant.getId(),
                        Common.currentRestaurant.getName(),
                        foodItemList.get(position).getName(),
                        foodItemList.get(position).getImage(),
                        foodItemList.get(position).getPrice())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(postFavorite -> {

                            if (postFavorite.isSuccess() && postFavorite.getResult().contains("Success"))
                            {
                                fav.setImageResource(R.drawable.ic_favorite_primary_color_24dp);
                                fav.setTag(true);

                                if (Common.currentFavOfRestaurant != null)
                                    Common.currentFavOfRestaurant.add(new FavoriteOnlyId(foodItemList.get(position).getId()));
                            }

                        },throwable -> {

                            Toast.makeText(context, "[ADD FAVORITE]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        })
                );
            }
        });

        holder.setCartClickListener((view, position1, isDetail) -> {
            if (isDetail) {
                context.startActivity(new Intent(context, FoodDetailActivity.class));
                EventBus.getDefault().postSticky(new FoodDetailEvent(true, foodItemList.get(position1)));
            } else {

                //create cart
                CartItem cartItem = new CartItem();
                cartItem.setFoodId(foodItemList.get(position).getId());
                cartItem.setFoodName(foodItemList.get(position).getName());
                cartItem.setFoodImage(foodItemList.get(position).getImage());
                cartItem.setFoodPrice(foodItemList.get(position).getPrice());
                cartItem.setFoodQuantity(1);
                cartItem.setUserPhone(Common.currentUser.getUserPhone());
                cartItem.setRestaurantId(Common.currentRestaurant.getId());
                cartItem.setFoodAddon("NORMAL");
                cartItem.setFoodSize("NORMAL");
                cartItem.setFoodExtraPrice(0.0);
                cartItem.setFbid(Common.currentUser.getFbid());

                compositeDisposable.add(
                        cartDataSource.inserOrReplaceAll(cartItem)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {

                                    Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();


                                }, throwable -> {
                                    Toast.makeText(context, "[ADD CART]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                })
                );
            }

        });


    }

    @Override
    public int getItemCount() {
        return foodItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.img_food)
        ImageView img_food;

        @BindView(R.id.img_cart)
        ImageView img_cart;

        @BindView(R.id.img_detail)
        ImageView img_detail;

        @BindView(R.id.img_favo)
        ImageView img_favo;

        @BindView(R.id.txt_food_name)
        TextView txt_food_name;

        @BindView(R.id.txt_food_price)
        TextView txt_food_price;

        IFoodDetailOrCartCliclListener cartClickListener;

        public void setCartClickListener(IFoodDetailOrCartCliclListener cartClickListener) {
            this.cartClickListener = cartClickListener;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            img_cart.setOnClickListener(this);
            img_detail.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.img_detail)
                cartClickListener.onFoodItemListener(view, getAdapterPosition(), true);
            else
                cartClickListener.onFoodItemListener(view, getAdapterPosition(), false);
        }
    }
}
