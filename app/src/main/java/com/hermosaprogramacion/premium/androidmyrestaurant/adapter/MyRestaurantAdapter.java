package com.hermosaprogramacion.premium.androidmyrestaurant.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hermosaprogramacion.premium.androidmyrestaurant.MenuActivity;
import com.hermosaprogramacion.premium.androidmyrestaurant.R;
import com.hermosaprogramacion.premium.androidmyrestaurant.common.Common;
import com.hermosaprogramacion.premium.androidmyrestaurant.interfac.ClickListener;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.RestaurantItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus.MenuItemEvent;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyRestaurantAdapter extends RecyclerView.Adapter<MyRestaurantAdapter.ViewHolder> {

    Context context;
    List<RestaurantItem> restaurantItems;


    public MyRestaurantAdapter(Context context, List<RestaurantItem> restaurantItems) {
        this.context = context;
        this.restaurantItems = restaurantItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_restaurant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        RestaurantItem restaurantItem = restaurantItems.get(position);

        Picasso.get().load(restaurantItem.getImage()).into(holder.imageView);

        holder.txt_restaurant_name.setText(restaurantItem.getName());

        holder.txt_restaurant_address.setText(restaurantItem.getAddress());

        holder.setListener(new ClickListener() {
            @Override
            public void listener(View view, int position) {

                Common.currentRestaurant = restaurantItems.get(position);

                EventBus.getDefault().postSticky(new MenuItemEvent(true, restaurantItems.get(position)));
                context.startActivity(new Intent(context, MenuActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.txt_restaurant_name)
        TextView txt_restaurant_name;

        @BindView(R.id.txt_restaurant_address)
        TextView txt_restaurant_address;

        @BindView(R.id.img_restaurant)
        ImageView imageView;

        ClickListener clickListener;

        public void setListener(ClickListener listener) {
            this.clickListener = listener;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.listener(v, getAdapterPosition());
        }
    }
}
