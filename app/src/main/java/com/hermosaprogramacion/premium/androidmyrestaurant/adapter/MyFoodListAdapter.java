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
import com.hermosaprogramacion.premium.androidmyrestaurant.interfac.IFoodDetailOrCartCliclListener;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.FoodItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyFoodListAdapter extends RecyclerView.Adapter<MyFoodListAdapter.ViewHolder> {

    Context context;
    List<FoodItem> foodItemList;

    public MyFoodListAdapter(Context context, List<FoodItem> foodItemList) {
        this.context = context;
        this.foodItemList = foodItemList;
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
            else
                Toast.makeText(context, "click Cart", Toast.LENGTH_SHORT).show();
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
