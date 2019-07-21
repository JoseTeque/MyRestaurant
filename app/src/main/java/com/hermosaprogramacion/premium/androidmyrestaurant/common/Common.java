package com.hermosaprogramacion.premium.androidmyrestaurant.common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hermosaprogramacion.premium.androidmyrestaurant.R;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.AddonItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.FavoriteItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.FavoriteOnlyId;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.RestaurantItem;
import com.hermosaprogramacion.premium.androidmyrestaurant.model.User;
import com.hermosaprogramacion.premium.androidmyrestaurant.retrofit.RetrofitClient;
import com.hermosaprogramacion.premium.androidmyrestaurant.service.IFcmService;
import com.hermosaprogramacion.premium.androidmyrestaurant.service.MyFirebaseMessagingService;

import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class Common {

    public static final String API_RESTAURANT_ENDPOINT = "http://192.168.8.103:3000/";
    public static final String API_KEY = "1234"; // we will set hard code API key now, but i will show you now to secure it with firebase remote config soon
    public static final int DEFAULT_COLUM_COUNT = 0;
    public static final int FULL_WIDTH_COLUM = 1 ;
    public static final String REMEMBER_FBID = "REMEMBER_FBID";
    public static final String APY_KEY_TAG = "APY_KEY";
    public static final String NOTIFI_TITLE = "title";
    public static final String NOTIFI_CONTENT = "content";

    public static User currentUser;
    public static RestaurantItem currentRestaurant;
    public static Set<AddonItem> addonList = new HashSet<>();
    public static List<FavoriteOnlyId> currentFavOfRestaurant;

    public static IFcmService getFCMService()
    {
        return RetrofitClient.getInstance("https://fcm.googleapis.com/").create(IFcmService.class);
    }

    public static boolean checkFavorite(int id) {
        boolean result = false;
        for (FavoriteOnlyId item : currentFavOfRestaurant)
            if (item.getFoodId() == id)
            {
                result = true;
            }
        return result;
    }

    public static void removeFavorite(int id) {

        for (FavoriteOnlyId item : currentFavOfRestaurant)
            if (item.getFoodId() == id)
            {
                currentFavOfRestaurant.remove(item);
            }

    }

    public static String converStatusToString(int orderStatus) {

        switch (orderStatus)
        {
            case 0:
                return "Placed";
            case 1:
                return "Shipping";
            case 2:
                return "shipped";
            case -1:
                return "cancelled";
                default:
                    return "cancelled";
        }
    }

    public static void showNotification(Context context, int notiId, String title, String body, Intent intent) {

        PendingIntent pendingIntent = null;
        if (intent != null)
            pendingIntent = PendingIntent.getActivity(context,notiId,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        String NOTIFICATIN_CHANEL_ID = "JDev_My_Restaurant";
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATIN_CHANEL_ID,
                    "My Restaurant Notification", NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("My Restaurant client App");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);

        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATIN_CHANEL_ID);

        builder.setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.app_icon));

        if (pendingIntent != null)
            builder.setContentIntent(pendingIntent);
        Notification mNotification = builder.build();

        notificationManager.notify(notiId,mNotification);

    }
}
