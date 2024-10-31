package com.example.soundsenseapp;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.soundsenseapp.Spotify.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.getWritableDatabase();
            Log.d("MainActivity", "Database connected successfully and tables created if not exist.");
        } catch (Exception e) {
            Log.e("MainActivity", "Database connection failed: " + e.getMessage());
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            loadFragment(newHomeFragmentInstance());
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.navigation_home) {
                selectedFragment = newHomeFragmentInstance();
            } else if (item.getItemId() == R.id.navigation_playlists) {
                selectedFragment = new PlaylistFragment();
            } else if (item.getItemId() == R.id.navigation_musicGPT) {
                selectedFragment = new MusicBotFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });

    }

    private HomeFragment newHomeFragmentInstance() {
        try {
            return new HomeFragment();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
