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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.soundsenseapp.Spotify.DatabaseHelper;
import com.example.soundsenseapp.Spotify.Playlist;
import com.example.soundsenseapp.Spotify.SongFormat;
import com.example.soundsenseapp.Spotify.SpotifyAPI;
import com.example.soundsenseapp.data.sensorData.Accelerometer;
import com.example.soundsenseapp.data.sensorData.GPSLocation;
import com.example.soundsenseapp.data.sensorData.Temperature;
import com.example.soundsenseapp.data.util.Genre;
import com.example.soundsenseapp.data.util.PlaylistViewModel;
import com.example.soundsenseapp.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class HomeFragment extends Fragment implements Temperature.TemperatureListener, Accelerometer.AccelerometerListener, GPSLocation.LocationListener {

    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    private Button logoutButton;
    private Button countryButton;
    private Button temperatureButton;
    private Button speedButton;
    private Button moodButton;
    private Button saveButton;

    private Temperature temperatureSensor;
    private Accelerometer accelerometer;
    private GPSLocation gpsLocation;

    private RecyclerView playlistRecyclerView;
    private PlaylistAdapter playlistAdapter;
    private SpotifyAPI spotifyAPI;
    private ArrayList<SongFormat> playlist = new ArrayList<>();
    private PlaylistViewModel playlistViewModel;

    private float currentSpeed;
    private float currentTemperature = 21;

    private TextView playlistNameTextView;

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
            saveButton = view.findViewById(R.id.save_button);

            gpsLocation = new GPSLocation(requireActivity(), this);
            playlistNameTextView = view.findViewById(R.id.playlist_name);

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
            moodButton.setOnClickListener(v -> getMood());
            saveButton.setOnClickListener(v -> {
                String playlistName = playlistNameTextView.getText().toString();
                if (playlist != null && !playlist.isEmpty() && !playlistName.isEmpty()) {
                    DatabaseHelper dbHelper = DatabaseHelper.getInstance(getContext());
                    ArrayList<Playlist> allPlaylists = DatabaseHelper.getAllPlaylists(dbHelper);
                    boolean playlistExists = false;
                    for (Playlist existingPlaylist : allPlaylists) {
                        if (existingPlaylist.getName().equals(playlistName)) {
                            playlistExists = true;
                            break;
                        }
                    }
                    if (playlistExists) {
                        Toast.makeText(requireActivity(), "Playlist already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        DatabaseHelper.savePlaylist(dbHelper, playlistName, playlist);
                        Toast.makeText(requireActivity(), "Playlist saved successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireActivity(), "No available playlist", Toast.LENGTH_SHORT).show();
                }
            });

            playlistViewModel = new ViewModelProvider(requireActivity()).get(PlaylistViewModel.class);
            playlistRecyclerView = view.findViewById(R.id.playlistRecyclerView);
            playlistRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            playlistAdapter = new PlaylistAdapter(playlist, song -> openSpotifyLink(song.getSpotifyLink()));
            playlistRecyclerView.setAdapter(playlistAdapter);
            playlistViewModel.getPlaylist().observe(getViewLifecycleOwner(), songs -> {
                playlistAdapter.setSongs(songs);
            });
            speedButton.setOnClickListener(v -> {
                Log.d("HomeFragment", "Speed button clicked");
                fetchSongsByEnergy(currentSpeed);
            });
            temperatureButton.setOnClickListener(v -> {
                Log.d("HomeFragment", "Temperature button clicked");
                fetchSongsByTemperature(currentTemperature);
            });

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
        currentTemperature = temperature;
        temperatureButton.setText(String.format("Temperature: %.1f°C", temperature));
    }

    @Override
    public void onTemperatureUnavailable() {
        temperatureButton.setText("Temperature sensor unavailable");
    }

    @Override
    public void onAccelerometerChanged(float velocity) {
        currentSpeed = velocity;
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

    private void generatePlaylistName(ArrayList<SongFormat> playlist) {
        StringBuilder songNames = new StringBuilder();
        StringBuilder artistsNames = new StringBuilder();
        for (SongFormat song : playlist) {
            songNames.append(song.getName()).append(", ");
            artistsNames.append(song.getArtist()).append(", ");
        }
        String prompt = "Generate a playlist name less than or equal than 5 words based on the following songs and corresponding artist's names: " + songNames.toString() + artistsNames.toString();
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
                        String playlistNameWord = response.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        playlistNameTextView.setText(playlistNameWord.trim());
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
                headers.put("Content-Type", "application/json");
                String apiKey = BuildConfig.CHATGPT_API_KEY;
                headers.put("Authorization", "Bearer " + apiKey);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(jsonObjectRequest);
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
                String apiKey = BuildConfig.CHATGPT_API_KEY;
                headers.put("Authorization", "Bearer " + apiKey);
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

    private void fetchSongsByEnergy(float speedData) {
        new Thread(() -> {
            try {
                playlist.clear();
                Random random = new Random();
                String randomGenre = Genre.getRandomGenre(random);

                int minEnergy = 0;
                int maxEnergy = 0;

                if (speedData >= 0 && speedData < 1) {
                    maxEnergy = 40;
                } else if (speedData >= 1 && speedData < 4) {
                    minEnergy = 40;
                    maxEnergy = 100;
                } else if (speedData >= 4 && speedData < 10) {
                    minEnergy = 100;
                    maxEnergy = 120;
                } else if (speedData >= 10 && speedData < 20) {
                    minEnergy = 120;
                    maxEnergy = 140;
                }


                if(spotifyAPI != null) {
                    playlist.addAll(spotifyAPI.getSongsByEnergy(randomGenre, minEnergy, maxEnergy, 15));
                    generatePlaylistName(playlist);
                    playlistViewModel.setPlaylist(playlist);
                } else {
                    Log.e("HomeFragment", "SpotifyAPI instance is null");
                }
                getActivity().runOnUiThread(() -> playlistAdapter.notifyDataSetChanged());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void fetchSongsByTemperature(float currentTemperature) {
        new Thread(() -> {
            try {
                playlist.clear();
                Random random = new Random();
                String randomGenre = Genre.getRandomGenre(random);

                int minTempo = (int) (currentTemperature * 9 / 5 + 32 -10);
                int maxTempo = (int) (currentTemperature * 9 / 5 + 32 + 10);;


                if(spotifyAPI != null) {
                    playlist.addAll(spotifyAPI.getSongsByTempo(randomGenre, minTempo, maxTempo, 15));
                    generatePlaylistName(playlist);
                    playlistViewModel.setPlaylist(playlist);
                } else {
                    Log.e("HomeFragment", "SpotifyAPI instance is null");
                }
                getActivity().runOnUiThread(() -> playlistAdapter.notifyDataSetChanged());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
