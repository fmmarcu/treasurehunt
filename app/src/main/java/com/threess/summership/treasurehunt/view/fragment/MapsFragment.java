package com.threess.summership.treasurehunt.view.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.threess.summership.treasurehunt.R;
import com.threess.summership.treasurehunt.model.Treasure;
import com.threess.summership.treasurehunt.viewmodel.TreasureViewModel;

import java.util.List;

import static androidx.core.content.ContextCompat.checkSelfPermission;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = MapsFragment.class.getSimpleName();

    //vars
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private boolean mZoomIn = true;

    private static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final NavController navController = Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                navController.navigate(R.id.action_mapsFragment_to_userProfileFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        return inflater.inflate(R.layout.fragment_maps, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        MapView mMapView = view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this);//when you already implement OnMapReadyCallback in your fragment

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    if (mZoomIn) {
                        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(20);

                        mMap.moveCamera(center);
                        mMap.animateCamera(zoom);

                        mZoomIn = false;
                    }

                    if (checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                }
            }
        };

        getLocationPermission();
    }
    private boolean checkMapServices() {
        Log.d(TAG, "checkMapServices called");
        if (isServicesOK()) {
            return isMapsEnabled();
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        Log.d(TAG, "buildAlertmessagenoGps called");
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean isMapsEnabled() {
        Log.d(TAG, "isMapsEnabled called");
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        Log.d(TAG, "getLocationPermission called");
        if (checkSelfPermission(this.getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            //getLastKnownLocation();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(getActivity(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult called");
        mLocationPermissionGranted = false;
        // If request is cancelled, the result arrays are empty.
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            }
        }
    }


    @Override
    public void onResume() {
        Log.d(TAG, "onResume called");
        super.onResume();
        if (checkMapServices()) {
            if (mLocationPermissionGranted) {
                mZoomIn = true;
                startLocationUpdates();
            } else {
                getLocationPermission();
            }
        }
    }

    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        if (checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        createLocationRequest();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,
                null /* Looper */);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady called");
        mMap = googleMap;

        TreasureViewModel treasureListViewModel = ViewModelProviders.of(this).get(TreasureViewModel.class);
        treasureListViewModel.init();
        Observer observer = new Observer<List<Treasure>>() {
            @Override
            public void onChanged(List<Treasure> treasures) {
                for (Treasure treasure : treasures) {
                    if (treasure.getClaimedBy().equals("")) {
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(treasure.getLocationLat(), treasure.getLocationLon()))
                                .title(treasure.getTitle()));
                    }
                }
            }
        };
        treasureListViewModel.getTreasureRepository().observe(this, observer);
    }
}
