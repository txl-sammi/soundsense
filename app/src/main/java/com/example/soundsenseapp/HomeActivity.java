package com.example.soundsenseapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soundsenseapp.data.sensorData.Accelerometer;
import com.example.soundsenseapp.data.sensorData.Temperature;
import com.example.soundsenseapp.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity implements Temperature.TemperatureListener, Accelerometer.AccelerometerListener {

    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private Button logoutButton;
    private TextView temperatureDataTextView;
    private TextView velocityDataTextView;
    private Temperature temperatureSensor;
    private Accelerometer accelerometer;

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
            temperatureDataTextView = findViewById(R.id.temperatureData);
            velocityDataTextView = findViewById(R.id.volecityData);

            temperatureSensor = new Temperature(this, this);
            temperatureSensor.startListening();

            accelerometer = new Accelerometer(this, this);
            accelerometer.startListening();

            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logout();
                }
            });
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
    }

    @Override
    public void onTemperatureChanged(float temperature) {
        temperatureDataTextView.setText(String.format(" %.1f°C", temperature));
    }

    @Override
    public void onTemperatureUnavailable() {
        temperatureDataTextView.setText("Temperature sensor unavailable");
    }

    @Override
    public void onAccelerometerChanged(float velocity) {
        velocityDataTextView.setText(String.format(" %.2f m/s", velocity));
    }

    @Override
    public void onAccelerometerUnavailable() {
        velocityDataTextView.setText("Accelerometer unavailable");
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
