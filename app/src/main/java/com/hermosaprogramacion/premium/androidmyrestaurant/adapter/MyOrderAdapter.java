package com.hermosaprogramacion.premium.androidmyrestaurant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hermosaprogramacion.premium.androidmyrestaurant.R;
import com.hermosaprogramacion.premium.androidmyrestaurant.common.Common;
import com.hermosaprogramacion.premium.androidmyrestaurant.interfac.ClickListener;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.OrderItem;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder> {

    private Context context;
    private List<OrderItem> orderItemList;
    private SimpleDateFormat simpleDateFormat;

    public MyOrderAdapter(Context context, List<OrderItem> orderItemList) {
        this.context = context;
        this.orderItemList = orderItemList;
        simpleDateFormat= new SimpleDateFormat("MM/dd/yyyy");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_order,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        OrderItem orderItem= orderItemList.get(position);

        holder.txt_order_Address.setText(orderItem.getOrderAddress());
        holder.txt_order_phone.setText(orderItem.getOrderPhone());
        holder.txt_order_status.setText(Common.converStatusToString(orderItem.getOrderStatus()));
        holder.txt_num_of_item.setText(new StringBuilder("Num of item: ").append(orderItem.getNumOfItem()));
        holder.txt_order_total_price.setText(new StringBuilder("$").append(orderItem.getTotalPRice()));
        holder.txt_order_number.setText(new StringBuilder("Num of orden: ").append(orderItem.getOrderId()));
        holder.txt_order_date.setText(new StringBuilder(simpleDateFormat.format(orderItem.getOrderDate())));

        if (orderItem.isCod())
            holder.txt_order_cod.setText(new StringBuilder("Cash on delivery"));
        else
            holder.txt_order_cod.setText(new StringBuilder("TransId: ").append(orderItem.getTransactionId()));


    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.txt_order_number)
        TextView txt_order_number;

        @BindView(R.id.txt_order_status)
        TextView txt_order_status;

        @BindView(R.id.txt_order_phone)
        TextView txt_order_phone;

        @BindView(R.id.txt_order_Address)
        TextView txt_order_Address;

        @BindView(R.id.txt_order_cod)
        TextView txt_order_cod;

        @BindView(R.id.txt_order_date)
        TextView txt_order_date;

        @BindView(R.id.txt_order_total_price)
        TextView txt_order_total_price;

        @BindView(R.id.txt_num_of_item)
        TextView txt_num_of_item;

        ClickListener clickListener;

        public void setClickListener(ClickListener clickListener) {
            this.clickListener = clickListener;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            clickListener.listener(v,getAdapterPosition());
        }
    }
}
