package com.threess.summership.treasurehunt.view.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RemoteViews;


import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.threess.summership.treasurehunt.R;
import com.threess.summership.treasurehunt.model.Treasure;
import com.threess.summership.treasurehunt.view.activity.MainActivity;
import com.threess.summership.treasurehunt.viewmodel.TreasureViewModel;

import static com.threess.summership.treasurehunt.Constants.CHANNEL_ID;
import static com.threess.summership.treasurehunt.Constants.NOTIFICATION_ID;

public class NotificationHandler extends Handler {
    private Context mContext;
    private Activity mMainActivity;
    private boolean notificationShow = true;
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder builder;
    private Boolean isInBackground = false;

    @Override
    public boolean sendMessageAtTime(@NonNull Message msg, long uptimeMillis) {
        return super.sendMessageAtTime(msg, uptimeMillis);
    }

    public NotificationHandler(Context context, Activity activity){
        mContext = context;
        mMainActivity = activity;
        notificationBuilder();
        if (Build.VERSION.SDK_INT >=   Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, importance);
            //Boolean value to set if lights are enabled for Notifications from this Channel
            notificationChannel.enableLights(true);
            //Boolean value to set if vibration are enabled for Notifications from this Channel
            notificationChannel.enableVibration(true);
            //Sets the color of Notification Light
            notificationChannel.setLightColor(Color.GREEN);
            //Set the vibration pattern for notifications. Pattern is in milliseconds with the format {delay,play,sleep,play,sleep...}
            notificationChannel.setVibrationPattern(new long[] {
                    500,
                    500,
                    500,
                    500,
                    500
            });
            //Sets whether notifications from these Channel should be visible on Lockscreen or not
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what == 0) {
            LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (locationManager != null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        float[] mDistance = new float[1];
                        TreasureViewModel mTreasureViewModel = ViewModelProviders.of((FragmentActivity) mContext).get(TreasureViewModel.class);
                        for (Treasure treasure :mTreasureViewModel.getTreasureRepository().getValue()) {
                            Location.distanceBetween(location.getLatitude(), location.getLongitude(), treasure.getLocationLat(), treasure.getLocationLon(), mDistance);
                            if (mDistance[0] < 0) {
                                mDistance[0] = mDistance[0] * (-1);
                            }
                            if (mDistance[0] <= 100 && !treasure.isClaimed()) {
                                showNotification();
                                if (isInBackground) {
                                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                                }
                                break;
                            }
                        }
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                });
            }
        }
    }


    @NonNull
    @Override
    public String getMessageName(Message message) {
        return super.getMessageName(message);
    }

    private void showNotification() {
        final NavController navController = Navigation.findNavController(mMainActivity, R.id.my_nav_host_fragment);
        if (!notificationShow) {
            final Snackbar snackbar = Snackbar.make(mMainActivity.findViewById(R.id.main), R.string.notificationTrasureNear, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(R.string.notificationShowOnMap, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    notificationShow = true;
                    navController.navigate(R.id.mapsFragment);
                }
            }).show();
        }
    }
    private void notificationBuilder() {
        RemoteViews collapsedView = new RemoteViews(mMainActivity.getPackageName(),
                R.layout.fragment_notification_collaps);
        RemoteViews expandedView = new RemoteViews(mMainActivity.getPackageName(),
                R.layout.fragment_notification_expand);
        builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager = NotificationManagerCompat.from(mContext);

        Intent clickIntent = new Intent(mContext, MainActivity.class);
        clickIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(mContext, 0,
                clickIntent, 0);
        expandedView.setOnClickPendingIntent(R.id.notificationShowTreasureOnMapButton, intent);
    }
    public void isInBackground(Boolean background){
        isInBackground = background;
    }
    public void setNotificationShow(Boolean bool){
        notificationShow = bool;
    }
}
