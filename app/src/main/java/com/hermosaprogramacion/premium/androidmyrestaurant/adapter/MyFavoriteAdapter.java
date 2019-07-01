package com.hermosaprogramacion.premium.androidmyrestaurant.adapter;

import android.app.AlertDialog;
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

import com.hermosaprogramacion.premium.androidmyrestaurant.FoodDetailActivity;
import com.hermosaprogramacion.premium.androidmyrestaurant.R;
import com.hermosaprogramacion.premium.androidmyrestaurant.common.Common;
import com.hermosaprogramacion.premium.androidmyrestaurant.interfac.ClickListener;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.FavoriteItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.RestaurantItem;
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

public class MyFavoriteAdapter extends RecyclerView.Adapter<MyFavoriteAdapter.ViewHolder> {

    private Context context;
    private List<FavoriteItem> favoriteItemList;
    private CompositeDisposable compositeDisposable;
    private IMyRestaurantAPI iMyRestaurantAPI;
    private AlertDialog dialog;

    public MyFavoriteAdapter(Context context, List<FavoriteItem> favoriteItemList) {
        this.context = context;
        this.favoriteItemList = favoriteItemList;
        compositeDisposable= new CompositeDisposable();
        iMyRestaurantAPI= RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
        dialog = new SpotsDialog.Builder().setContext(context).setCancelable(false).build();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_favorite,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Picasso.get().load(favoriteItemList.get(position).getFoodImage()).into(holder.img_favorite);

        holder.txt_name_favorite.setText(favoriteItemList.get(position).getFoodName());

        holder.txt_price_favorite.setText(new StringBuilder(context.getString(R.string.money_sign)).append(favoriteItemList.get(position).getPrice()));

        holder.txt_name_restaurant_favorite.setText(favoriteItemList.get(position).getRestaurantName());

        //Event
        holder.setClistener((view, position1) -> {

            dialog.show();
            compositeDisposable.add(iMyRestaurantAPI.getFoodById(Common.API_KEY, favoriteItemList.get(position1).getFoodId())
            .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(food -> {
                        if (food.isSucces())
                        {
                          context.startActivity(new Intent(context, FoodDetailActivity.class));

                          if (Common.currentRestaurant == null)
                              Common.currentRestaurant = new RestaurantItem();

                          Common.currentRestaurant.setId(favoriteItemList.get(position1).getRestaurantId());
                          Common.currentRestaurant.setName(favoriteItemList.get(position1).getRestaurantName());
                            EventBus.getDefault().postSticky(new FoodDetailEvent(true, food.getResult().get(0)));
                        }
                        else
                        {
                            Toast.makeText(context, "[LOAD FOOD FAVORITE RESULT]" + food.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    },throwable -> {
                        dialog.dismiss();
                        Toast.makeText(context, "[LOAD FOOD FAVORITE]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    })

            );
        });
    }

    @Override
    public int getItemCount() {
        return favoriteItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.img_favorite)
        ImageView img_favorite;

        @BindView(R.id.txt_name_favorite)
        TextView txt_name_favorite;

        @BindView(R.id.txt_price_favorite)
        TextView txt_price_favorite;

        @BindView(R.id.txt_name_restaurant_favorite)
        TextView txt_name_restaurant_favorite;

        ClickListener clistener;

        public void setClistener(ClickListener clistener) {
            this.clistener = clistener;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clistener.listener(v,getAdapterPosition());
        }
    }

    public void onDestroy()
    {
        compositeDisposable.clear();
    }
}
