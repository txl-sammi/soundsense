package com.example.sensor_gps;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sensor_gps.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    private ActivityMainBinding binding;
    // Google's API for location services
    private FusedLocationProviderClient fusedLocationClient;
    // Configuration of all settings of FusedLocationProviderClient
    LocationRequest locationRequest;
    LocationCallback locationCallback;

    private TextView countryTextView;
    private TextView cityTextView;

    private final int Request_Code_Location = 22;

    private TextView latttv;
    private TextView longtv;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        countryTextView = findViewById(R.id.countryTextView);
        cityTextView = findViewById(R.id.cityTextView);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 권한 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //startLocationUpdates();
        } else {
            // 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        }

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);

        locationCallback = new LocationCallback()
        {
            @Override
            public void onLocationResult (@NonNull LocationResult locationResult){
                super.onLocationResult(locationResult);
                if (locationResult != null) {
                    Log.d("LocationTest", "Location updates");
                    updateUI(locationResult.getLastLocation());
                } else {
                    Log.d("LocationTest", "Location updates fail: null");
                }
            }
        };

        latttv = binding.latitudeInput;
        longtv = binding.longitudeInput;
        button = binding.button;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateLocation();
            }
        });
    }

    private void updateLocation()
    {
        // if user grants permission
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            // get the last location
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location == null) {
                                Log.d("LocationTest", "null");
                            } else {
                                Log.d("LocationTest", "Success");
                                updateUI(location);
                            }
                        }
                    });
        }
        else {
            // if the user hasn't granted permission, ask for it explicitly
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_Code_Location);
        }
    }

    private void updateUI(Location location)
    {
        latttv.setText(String.valueOf(location.getLatitude()));
        longtv.setText(String.valueOf(location.getLongitude()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == Request_Code_Location){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                updateLocation();
            }
        }
    }
}