package com.example.soundsenseapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.soundsenseapp.data.sensorData.Accelerometer;
import com.example.soundsenseapp.data.sensorData.GPSLocation;
import com.example.soundsenseapp.data.sensorData.Temperature;
import com.example.soundsenseapp.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity implements Temperature.TemperatureListener, Accelerometer.AccelerometerListener, GPSLocation.LocationListener {

    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    private Button logoutButton;
    private Button countryButton;
    private Button temperatureButton;
    private Button speedButton;

    private Temperature temperatureSensor;
    private Accelerometer accelerometer;
    private GPSLocation gpsLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean(KEY_IS_LOGGED_IN, false);

        if (!isLoggedIn) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_home);

            logoutButton = findViewById(R.id.logoutButton);
            countryButton = findViewById(R.id.country_button);
            temperatureButton = findViewById(R.id.temperature_button);
            speedButton = findViewById(R.id.speed_button);

            gpsLocation = new GPSLocation(this, this);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
            } else {
                gpsLocation.startListening();
            }


            temperatureSensor = new Temperature(this, this);
            temperatureSensor.startListening();

            accelerometer = new Accelerometer(this, this);
            accelerometer.startListening();

            logoutButton.setOnClickListener(v -> logout());

            countryButton.setOnClickListener(v -> Toast.makeText(HomeActivity.this, "Country: " + countryButton.getText().toString(), Toast.LENGTH_SHORT).show());
            temperatureButton.setOnClickListener(v -> Toast.makeText(HomeActivity.this, "Temperature sensor is active", Toast.LENGTH_SHORT).show());
            speedButton.setOnClickListener(v -> Toast.makeText(HomeActivity.this, "Speed sensor is active", Toast.LENGTH_SHORT).show());
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            gpsLocation.startListening();
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (temperatureSensor != null) {
            temperatureSensor.stopListening();
        }
        if (accelerometer != null) {
            accelerometer.stopListening();
        }
        if (gpsLocation != null) {
            gpsLocation.stopListening();
        }
    }

    @Override
    public void onLocationChanged(String countryName) {
        countryButton.setText("Location: " + countryName);
    }

    @Override
    public void onTemperatureChanged(float temperature) {
        temperatureButton.setText(String.format("Temperature: %.1f°C", temperature));
    }

    @Override
    public void onTemperatureUnavailable() {
        temperatureButton.setText("Temperature sensor unavailable");
    }

    @Override
    public void onAccelerometerChanged(float velocity) {
        speedButton.setText(String.format("Velocity: %.2f m/s", velocity));
    }

    @Override
    public void onAccelerometerUnavailable() {
        speedButton.setText("Accelerometer unavailable");
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
