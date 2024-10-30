package com.example.soundsenseapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.soundsenseapp.Spotify.SongFormat;
import com.example.soundsenseapp.Spotify.SpotifyAPI;
import com.example.soundsenseapp.data.sensorData.Accelerometer;
import com.example.soundsenseapp.data.sensorData.GPSLocation;
import com.example.soundsenseapp.data.sensorData.Temperature;
import com.example.soundsenseapp.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements Temperature.TemperatureListener, Accelerometer.AccelerometerListener, GPSLocation.LocationListener {

    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    private Button logoutButton;
    private Button countryButton;
    private Button temperatureButton;
    private Button speedButton;
    private Button moodButton;

    private Temperature temperatureSensor;
    private Accelerometer accelerometer;
    private GPSLocation gpsLocation;

    private RecyclerView playlistRecyclerView;
    private PlaylistAdapter playlistAdapter;
    private SpotifyAPI spotifyAPI;
    private ArrayList<SongFormat> playlist = new ArrayList<>();


    public HomeFragment() throws JSONException, IOException {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, requireActivity().MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean(KEY_IS_LOGGED_IN, false);

        if (!isLoggedIn) {
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        } else {
            logoutButton = view.findViewById(R.id.logoutButton);
            countryButton = view.findViewById(R.id.country_button);
            temperatureButton = view.findViewById(R.id.temperature_button);
            speedButton = view.findViewById(R.id.speed_button);
            moodButton = view.findViewById(R.id.mood_button);

            gpsLocation = new GPSLocation(requireActivity(), this);

            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
            } else {
                gpsLocation.startListening();
            }

            temperatureSensor = new Temperature(requireActivity(), this);
            temperatureSensor.startListening();

            accelerometer = new Accelerometer(requireActivity(), this);
            accelerometer.startListening();

            logoutButton.setOnClickListener(v -> logout());

            countryButton.setOnClickListener(v -> Toast.makeText(requireActivity(), "Country: " + countryButton.getText().toString(), Toast.LENGTH_SHORT).show());
            temperatureButton.setOnClickListener(v -> Toast.makeText(requireActivity(), "Temperature sensor is active", Toast.LENGTH_SHORT).show());
            speedButton.setOnClickListener(v -> Toast.makeText(requireActivity(), "Speed sensor is active", Toast.LENGTH_SHORT).show());
            moodButton.setOnClickListener(v -> getMood());

            playlistRecyclerView = view.findViewById(R.id.playlistRecyclerView);
            playlistRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            playlistAdapter = new PlaylistAdapter(playlist, song -> openSpotifyLink(song.getSpotifyLink()));
            playlistRecyclerView.setAdapter(playlistAdapter);
            Button speedButton = view.findViewById(R.id.speed_button);
            speedButton.setOnClickListener(v -> fetchSongsByEnergy());

            try {
                spotifyAPI = new SpotifyAPI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            gpsLocation.startListening();
        } else {
            Toast.makeText(requireActivity(), "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
        SharedPreferences preferences = requireActivity().getSharedPreferences("UserPrefs", requireActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void getMood() {
        float temperature = temperatureSensor.getCurrentTemp();
        float speed = accelerometer.getCurrentVelo();
        String location = countryButton.getText().toString();

        String prompt = "Given temperature is " + temperature + "°C，velocity is " + speed + "m/s，location is " + location + ", give a word to describe my mood.";
        sendMoodRequest(prompt);
    }

    private void sendMoodRequest(String prompt) {
        String url = "https://api.openai.com/v1/chat/completions";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("model", "gpt-3.5-turbo");

            JSONArray messagesArray = new JSONArray();
            JSONObject messageObject = new JSONObject();
            messageObject.put("role", "user");
            messageObject.put("content", prompt);
            messagesArray.put(messageObject);

            jsonObject.put("messages", messagesArray);
            jsonObject.put("max_tokens", 10);
            jsonObject.put("temperature", 0.5);
        } catch (Exception e) {
            Log.e("HomeFragment", "Error creating JSON object: " + e.getMessage());
        }

        @SuppressLint("SetTextI18n") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                response -> {
                    try {
                        String moodWord = response.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        moodButton.setText("Mood is " + moodWord.trim());
                    } catch (Exception e) {
                        Log.e("HomeFragment", "Error parsing response: " + e.getMessage());
                    }
                },
                error -> {
                    if (error.networkResponse != null) {
                        String errorMsg = new String(error.networkResponse.data);
                        Log.e("HomeFragment", "Error: " + errorMsg);
                    } else {
                        Log.e("HomeFragment", "Error: " + error.getMessage());
                    }
                }
        ) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.HashMap<String, String> headers = new java.util.HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer ");
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(jsonObjectRequest);
    }

    private void openSpotifyLink(String link) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        intent.setPackage("com.spotify.music");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
        }
    }

    private void fetchSongsByEnergy() {
        new Thread(() -> {
            try {
                playlist.clear();
                playlist.addAll(spotifyAPI.getSongsByEnergy("pop", 0, 80, 10));
                getActivity().runOnUiThread(() -> playlistAdapter.notifyDataSetChanged());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
