package com.hermosaprogramacion.premium.androidmyrestaurant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.hermosaprogramacion.premium.androidmyrestaurant.adapter.MyOrderAdapter;
import com.hermosaprogramacion.premium.androidmyrestaurant.common.Common;
import com.hermosaprogramacion.premium.androidmyrestaurant.database.CartDataSource;
import com.hermosaprogramacion.premium.androidmyrestaurant.interfac.ILoadMore;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.OrderItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.IMyRestaurantAPI;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.RetrofitClient;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ViewOrderActivity extends AppCompatActivity implements ILoadMore {

    @BindView(R.id.recycler_order)
    RecyclerView recycler_order;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;

    MyOrderAdapter myOrderAdapter;
    List<OrderItem> orderItemList;

    int maxData = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);

        init();
        initView();

       // getAllOrders();

        getMaxOrder();
    }

    private void getMaxOrder() {
        dialog.show();

        compositeDisposable.add(myRestaurantAPI.getMaxOrder(Common.API_KEY, Common.currentUser.getFbid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(maxOrder -> {

                    if (maxOrder.isSuccess())
                    {
                       maxData = maxOrder.getResult().get(0).getMaxRowNum();
                        dialog.dismiss();
                        getAllOrders(0,10);
                    }
                    else
                    {
                        Toast.makeText(this, " " + maxOrder.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();

                },throwable -> {
                    dialog.dismiss();
                    Toast.makeText(this, "[GET ORDER]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                })
        );
    }

    private void getAllOrders(int from, int to) {
    dialog.show();

    compositeDisposable.add(myRestaurantAPI.getOrder(Common.API_KEY, Common.currentUser.getFbid(),from,to)
    .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(order -> {

                if (order.isSuccess())
                {
                    if (order.getResult().size() > 0)
                    {
                        if (myOrderAdapter == null)
                        {
                            orderItemList = new ArrayList<>();
                            orderItemList = order.getResult();
                            myOrderAdapter= new MyOrderAdapter(this, orderItemList, recycler_order);
                            myOrderAdapter.setiLoadMore(this);
                            recycler_order.setAdapter(myOrderAdapter);
                        }
                        else
                        {
                            orderItemList.remove(orderItemList.size()-1);
                            orderItemList = order.getResult();
                            myOrderAdapter.addItem(orderItemList);
                        }

                    }
                    else
                    {
                        Toast.makeText(this, "Order empty", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(this, "[GET ORDER RESULT]" + order.getMessage(), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            },throwable -> {
                dialog.dismiss();
                Toast.makeText(this, "[GET ORDER]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            })
    );
    }

    private void initView() {
        ButterKnife.bind(this);

        toolbar.setTitle(getString(R.string.order));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recycler_order.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_order.setLayoutManager(layoutManager);
        recycler_order.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
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

    @Override
    public void onLoadMore() {
        if (myOrderAdapter.getItemCount() < maxData)
        {
            orderItemList.add(null);
            myOrderAdapter.notifyItemInserted(orderItemList.size()-1);

            getAllOrders(myOrderAdapter.getItemCount() + 1, myOrderAdapter.getItemCount() + 10 );
            myOrderAdapter.notifyDataSetChanged();
            myOrderAdapter.setLoaded();
        }
        else
        {
            Toast.makeText(this, "Max data to load..", Toast.LENGTH_SHORT).show();
        }
    }
}
