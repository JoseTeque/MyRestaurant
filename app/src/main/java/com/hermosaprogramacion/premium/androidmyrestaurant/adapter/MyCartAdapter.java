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
import com.hermosaprogramacion.premium.androidmyrestaurant.database.CartDataSource;
import com.hermosaprogramacion.premium.androidmyrestaurant.database.CartDatabase;
import com.hermosaprogramacion.premium.androidmyrestaurant.database.CartItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.database.LocalCartDataSource;
import com.hermosaprogramacion.premium.androidmyrestaurant.interfac.IonImageViewAdapterClickListener;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus.CalculatePriceEvent;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {
    private Context context;
    private List<CartItem> cartItemList;
    private CartDataSource cartDataSource;

    public MyCartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDao());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_cart,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Picasso.get().load(cartItemList.get(position).getFoodImage()).into(holder.img_food_cart);
        holder.txt_food_name_cart.setText(cartItemList.get(position).getFoodName());
        holder.txt_food_price_cart.setText(String.valueOf(cartItemList.get(position).getFoodPrice()));
        holder.txt_quantity.setText(String.valueOf(cartItemList.get(position).getFoodQuantity()));

        Double finalResult= cartItemList.get(position).getFoodPrice() * cartItemList.get(position).getFoodQuantity();
        holder.txt_new_price_cart.setText(String.valueOf(finalResult));
        holder.txt_extra_price_cart.setText(new StringBuilder("Extra Price($): +").append(cartItemList.get(position).getFoodExtraPrice()));

        //Event

        holder.setClickListener((view, position1, isDecreace, isDelete) -> {
            //delete item
            if (!isDelete)
            {
                // if not button delete food from cart click
                if (isDecreace)//if decrease quantity
                {
                  if (cartItemList.get(position1).getFoodQuantity() > 1)
                  {
                      cartItemList.get(position1).setFoodQuantity(cartItemList.get(position1).getFoodQuantity()-1);
                  }
                }
                else //if increase quantity
                {
                    if (cartItemList.get(position1).getFoodQuantity() < 99)
                    {
                        cartItemList.get(position1).setFoodQuantity(cartItemList.get(position1).getFoodQuantity()+1);
                    }
                }

                //update cart
                cartDataSource.updateCart(cartItemList.get(position1))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(Integer integer) {
                               holder.txt_quantity.setText(String.valueOf(cartItemList.get(position1).getFoodQuantity()));

                                EventBus.getDefault().postSticky(new CalculatePriceEvent());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(context, "[UPDATE CART]"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            else
                cartDataSource.deleteCart(cartItemList.get(position1))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(Integer integer) {
                              notifyItemRemoved(position1);
                              EventBus.getDefault().postSticky(new CalculatePriceEvent());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(context, "[DELETE ITEM]"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.img_food_cart)
        ImageView img_food_cart;

        @BindView(R.id.img_decrease)
        ImageView img_decrease;

        @BindView(R.id.img_add)
        ImageView img_add;

        @BindView(R.id.img_delete_food)
        ImageView img_delete_food;


        @BindView(R.id.txt_food_name_cart)
        TextView txt_food_name_cart;

        @BindView(R.id.txt_food_price_cart)
        TextView txt_food_price_cart;

        @BindView(R.id.txt_quantity)
        TextView txt_quantity;

        @BindView(R.id.txt_new_price_cart)
        TextView txt_new_price_cart;

        @BindView(R.id.txt_extra_price_cart)
        TextView txt_extra_price_cart;

        IonImageViewAdapterClickListener clickListener;

        public void setClickListener(IonImageViewAdapterClickListener clickListener) {
            this.clickListener = clickListener;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);

            img_decrease.setOnClickListener(this);
            img_delete_food.setOnClickListener(this);
            img_add.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
           if (v == img_decrease)
           {
               clickListener.onCalcularPriceListener(v,getAdapterPosition(),true,false);
           }
           else if (v == img_add)
           {
               clickListener.onCalcularPriceListener(v,getAdapterPosition(),false,false);
           }
           else if (v == img_delete_food)
           {
               clickListener.onCalcularPriceListener(v,getAdapterPosition(),false,true);
           }
        }
    }
}
