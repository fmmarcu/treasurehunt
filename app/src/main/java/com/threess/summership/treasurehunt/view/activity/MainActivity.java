package com.threess.summership.treasurehunt.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleObserver;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.view.View;

import com.facebook.stetho.Stetho;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.threess.summership.treasurehunt.R;
import com.threess.summership.treasurehunt.view.fragment.NotificationHandler;
import com.threess.summership.treasurehunt.view.fragment.NotificationReceiver;
import com.threess.summership.treasurehunt.viewmodel.LoginViewModel;
import com.threess.summership.treasurehunt.viewmodel.TreasureViewModel;

import java.util.Objects;

import static com.threess.summership.treasurehunt.Constants.NONE;
import static com.threess.summership.treasurehunt.Constants.SHARED_PREFERENCE_NAME;
import static com.threess.summership.treasurehunt.view.fragment.MapsFragment.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements LifecycleObserver {
    NotificationHandler notificationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TreasureViewModel mTreasureViewModel = ViewModelProviders.of(this).get(TreasureViewModel.class);
        mTreasureViewModel.init();
        Stetho.initializeWithDefaults(this);

        LoginViewModel loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        if (!getSharedPreferenceUsername().equals(NONE)) loginViewModel.authenticate(true);

        //Create the NavController
        final NavController navController = Navigation.findNavController(this, R.id.my_nav_host_fragment);
        //Create bottom navigation component
        final BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        //Link NavController with bottom Navigation
        NavigationUI.setupWithNavController(bottomNav, navController);
        //HideBottom Navigation on fragments that don't need it
        notificationHandler = new NotificationHandler(this,this);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller,
                                             @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.treasureListFragment ||
                        destination.getId() == R.id.userProfileFragment ||
                        destination.getId() == R.id.hallOfFameFragment ||
                        destination.getId() == R.id.mapsFragment) {
                    bottomNav.setVisibility(View.VISIBLE);
                    notificationHandler.setNotificationShow(false);
                } else {
                    bottomNav.setVisibility(View.GONE);
                    notificationHandler.setNotificationShow(true);
                }
                if(destination.getId() == R.id.mapsFragment ) {
                    notificationHandler.setNotificationShow(true);
                }
            }
        });
        NotificationReceiver.setMainActivity(this);
        getLocation();
    }


    private void getLocation() {
        getLocationPermission();
        if (getLocationPermission() == true) {
            notificationHandler.sendEmptyMessage(0);
        }
    }

    private boolean getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        notificationHandler.isInBackground(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        notificationHandler.isInBackground(false);
    }

    private String getSharedPreferenceUsername() {
        SharedPreferences sharedPreferences = Objects.requireNonNull(getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE));
        String referenceFileKey = getResources().getString(R.string.preference_file_key_username);
        if (!sharedPreferences.getString(getString(R.string.preference_file_key_username), referenceFileKey).isEmpty()) {
            return sharedPreferences.getString(getString(R.string.preference_file_key_username), referenceFileKey);
        }
        return NONE;
    }
}

