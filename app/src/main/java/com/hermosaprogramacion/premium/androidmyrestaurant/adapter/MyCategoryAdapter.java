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

import com.hermosaprogramacion.premium.androidmyrestaurant.FoodListActivity;
import com.hermosaprogramacion.premium.androidmyrestaurant.R;
import com.hermosaprogramacion.premium.androidmyrestaurant.common.Common;
import com.hermosaprogramacion.premium.androidmyrestaurant.interfac.ClickListener;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.MenuItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus.FoodListEvent;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyCategoryAdapter extends RecyclerView.Adapter<MyCategoryAdapter.ViewHolder> {

  private Context context;
  private List<MenuItem> menuItemList;

    public MyCategoryAdapter(Context context, List<MenuItem> menuItemList) {
        this.context = context;
        this.menuItemList = menuItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_category,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
           MenuItem item= menuItemList.get(position);
        Picasso.get().load(item.getImage()).into(holder.img_category);
        holder.txt_category.setText(item.getName());

        holder.setClickListener(new ClickListener() {
            @Override
            public void listener(View view, int position) {

                EventBus.getDefault().postSticky(new FoodListEvent(true, menuItemList.get(position) ));
                context.startActivity(new Intent(context, FoodListActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (menuItemList.size() == 1)
          return Common.DEFAULT_COLUM_COUNT;
        else
        {
            if (menuItemList.size() % 2 == 0)
            {
                return Common.DEFAULT_COLUM_COUNT;
            }else {
                return (position > 1 && position == menuItemList.size()-1) ? Common.FULL_WIDTH_COLUM:Common.DEFAULT_COLUM_COUNT;
            }
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.img_category)
        ImageView img_category;

        @BindView(R.id.txt_category)
        TextView txt_category;

        ClickListener clickListener;

        public void setClickListener(ClickListener clickListener) {
            this.clickListener = clickListener;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.listener(v,getAdapterPosition());
        }
    }
}
