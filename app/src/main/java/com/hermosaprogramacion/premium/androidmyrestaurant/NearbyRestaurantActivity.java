package com.hermosaprogramacion.premium.androidmyrestaurant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.media.ResourceBusyException;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hermosaprogramacion.premium.androidmyrestaurant.common.Common;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.NearbyRestaurantItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.eventBus.MenuItemEvent;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.IMyRestaurantAPI;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.RetrofitClient;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class NearbyRestaurantActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;

    LocationRequest locationRequest;
    LocationCallback locationCallback;
    FusedLocationProviderClient providerClient;
    Location currentlocation;

    Marker marker;

    boolean isFirstLoad = false;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        providerClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_restaurant);

        init();
        initView();

    }

    private void initView() {
        ButterKnife.bind(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        toolbar.setTitle(getString(R.string.nearby));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);

        buildLocationRequest();
        buildLocationCallback();

        providerClient = LocationServices.getFusedLocationProviderClient(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        providerClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                currentlocation = locationResult.getLastLocation();
                addMarkerAndMoveCamare(locationResult.getLastLocation());

                if (!isFirstLoad) {
                    isFirstLoad = !isFirstLoad;

                    requestNearbyRestaurant(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude(), 10);
                }

            }
        };
    }

    private void requestNearbyRestaurant(double latitude, double longitude, int distance) {
        dialog.show();
        compositeDisposable.add(myRestaurantAPI.getNearbyrestaurant(Common.API_KEY,latitude,longitude,distance)
        .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(nearbyRestaurant -> {

                    if (nearbyRestaurant.isSucces())
                    {
                        addMarkerRestaurant(nearbyRestaurant.getResult());

                    }else
                    {
                        Toast.makeText(this, "[]" + nearbyRestaurant.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();

                }, throwable -> {
                    dialog.dismiss();
                    Toast.makeText(this, "[NEARBY RESTAURANT]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                })
        );

    }

    private void addMarkerRestaurant(List<NearbyRestaurantItem> restaurantList) {

        for (NearbyRestaurantItem restaurantItem : restaurantList)
        {
            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_marker))
                    .position(new LatLng(restaurantItem.getLat(), restaurantItem.getLng()))
            .snippet(restaurantItem.getAddress())
            .title(new StringBuilder()
            .append(restaurantItem.getId())
            .append(".")
            .append(restaurantItem.getName()).toString()));
        }
    }

    private void addMarkerAndMoveCamare(Location lastLocation) {
        if (marker != null) {
            marker.remove();
        }

        LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(Common.currentUser.getName()));
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLng, 17f);
        mMap.animateCamera(yourLocation);
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.getUiSettings().setZoomControlsEnabled(true);

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.map_style));
            if (!success)
            {
                Log.e("ERROR_MAP", "Load style error..");
            }
        }catch (Resources.NotFoundException e)
        {
            Log.e("ERROR_MAP", "Resources not fund..");
        }

        mMap.setOnInfoWindowClickListener(marker -> {
            String id= marker.getTitle().substring(0,marker.getTitle().indexOf("."));
            if (!TextUtils.isEmpty(id))
            {
              compositeDisposable.add(myRestaurantAPI.getRestaurantById(Common.API_KEY, id)
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(restaurant -> {
                  if (restaurant.isSucces())
                  {
                      Common.currentRestaurant = restaurant.getResult().get(0);
                      EventBus.getDefault().postSticky(new MenuItemEvent(true,Common.currentRestaurant));
                      startActivity(new Intent(NearbyRestaurantActivity.this, MenuActivity.class));
                      finish();
                  }
                  else
                  {
                      Toast.makeText(this, "[]" + restaurant.getMessage(), Toast.LENGTH_SHORT).show();
                  }

              },throwable -> {
                  Toast.makeText(this, "[GET RESTAURANT BY ID]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
              }));
            }
        });
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
}
