package com.hermosaprogramacion.premium.androidmyrestaurant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hermosaprogramacion.premium.androidmyrestaurant.R;
import com.hermosaprogramacion.premium.androidmyrestaurant.common.Common;
import com.hermosaprogramacion.premium.androidmyrestaurant.database.CartDataSource;
import com.hermosaprogramacion.premium.androidmyrestaurant.database.CartDatabase;
import com.hermosaprogramacion.premium.androidmyrestaurant.database.CartItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.database.LocalCartDataSource;
import com.hermosaprogramacion.premium.androidmyrestaurant.interfac.IFoodDetailOrCartCliclListener;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.FoodItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MyFoodListAdapter extends RecyclerView.Adapter<MyFoodListAdapter.ViewHolder> {

    Context context;
    List<FoodItem> foodItemList;

    CompositeDisposable compositeDisposable;
    CartDataSource cartDataSource;

    public void onStop(){
        compositeDisposable.clear();
    }

    public MyFoodListAdapter(Context context, List<FoodItem> foodItemList) {
        this.context = context;
        this.foodItemList = foodItemList;
        compositeDisposable= new CompositeDisposable();
        cartDataSource= new LocalCartDataSource(CartDatabase.getInstance(context).cartDao());
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_food_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem item= foodItemList.get(position);

        holder.txt_food_name.setText(item.getName());
        Picasso.get().load(item.getImage()).placeholder(R.drawable.app_icon).into(holder.img_food);
        holder.txt_food_price.setText(new StringBuilder(context.getString(R.string.money_sign)).append(item.getPrice()));

        holder.setCartClickListener((view, position1, isDetail) -> {
            if (isDetail)
                Toast.makeText(context, "click Detail", Toast.LENGTH_SHORT).show();
            else {

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

                  compositeDisposable.add(
                          cartDataSource.inserOrReplaceAll(cartItem)
                          .subscribeOn(Schedulers.io())
                          .observeOn(AndroidSchedulers.mainThread())
                          .subscribe(()-> {

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

            ButterKnife.bind(this,itemView);

            img_cart.setOnClickListener(this);
            img_detail.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.img_detail)
                cartClickListener.onFoodItemListener(view,getAdapterPosition(),true);
            else
                cartClickListener.onFoodItemListener(view,getAdapterPosition(),false);
        }
    }


}
