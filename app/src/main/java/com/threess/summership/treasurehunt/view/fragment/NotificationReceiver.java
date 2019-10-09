package com.threess.summership.treasurehunt.view.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.threess.summership.treasurehunt.R;

import static com.threess.summership.treasurehunt.Constants.NOTIFICATION_ID;

public class NotificationReceiver extends BroadcastReceiver {
    private static Activity mMainActivity;
    @Override
    public void onReceive(Context context, Intent intent) {
        final NavController navController = Navigation.findNavController(mMainActivity, R.id.my_nav_host_fragment);
        navController.navigate(R.id.mapsFragment);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(NOTIFICATION_ID);
    }
    public static void setMainActivity(Activity mainActivity){
        mMainActivity = mainActivity;
    }

}
