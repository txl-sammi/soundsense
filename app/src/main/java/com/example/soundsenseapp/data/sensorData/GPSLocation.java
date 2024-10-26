package com.example.soundsenseapp.data.sensorData;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GPSLocation {

    private static final String TAG = "GPSLocation";
    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private final LocationListener listener;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    public GPSLocation(Context context, LocationListener listener) {
        this.context = context;
        this.listener = listener;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    for (android.location.Location location : locationResult.getLocations()) {
                        updateCountryName(location);
                    }
                }
            }
        };
    }

    public void startListening() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopListening() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void updateCountryName(android.location.Location location) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                String countryName = addresses.get(0).getCountryName();
                listener.onLocationChanged(countryName);
            } else {
                listener.onLocationChanged("Country not found");
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder failed", e);
            listener.onLocationChanged("Geocoder failed");
        }
    }

    public interface LocationListener {
        void onLocationChanged(String countryName);
    }
}
