package com.hermosaprogramacion.premium.androidmyrestaurant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hermosaprogramacion.premium.androidmyrestaurant.R;
import com.hermosaprogramacion.premium.androidmyrestaurant.common.Common;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.AddonItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus.AddonEventChange;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyAddonAdapter extends RecyclerView.Adapter<MyAddonAdapter.ViewHolder> {

   private   Context context;
   private   List<AddonItem> addonItemList;

    public MyAddonAdapter(Context context, List<AddonItem> addonItemList) {
        this.context = context;
        this.addonItemList = addonItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_addon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.chb_addon.setText(new StringBuilder(addonItemList.get(position).getName())
        .append(" +(" + context.getString(R.string.money_sign))
                .append(addonItemList.get(position).getExtraPrice())
                .append(")"));

        holder.chb_addon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                {
                    Common.addonList.add(addonItemList.get(position));
                    EventBus.getDefault().postSticky(new AddonEventChange(true, addonItemList.get(position)));
                }
                else
                {
                    Common.addonList.remove(addonItemList.get(position));
                    EventBus.getDefault().postSticky(new AddonEventChange(false, addonItemList.get(position)));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return addonItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.chb_addon)
        CheckBox chb_addon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
