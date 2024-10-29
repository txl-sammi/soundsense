package com.example.soundsenseapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            } else if (item.getItemId() == R.id.navigation_user) {
                selectedFragment = new UserFragment();
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
