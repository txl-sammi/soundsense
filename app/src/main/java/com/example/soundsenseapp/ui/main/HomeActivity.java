package com.example.soundsenseapp.ui.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.soundsenseapp.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use ViewBinding to inflate the layout
        com.example.soundsenseapp.databinding.ActivityHomeBinding binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the mood name and description dynamically
        binding.moodName.setText("Relaxed Mood");
        binding.moodDescription.setText("Recommended playlists for your relaxed mood");

        // Set OnClickListeners for each playlist option
        binding.playlist1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle playlist 1 click event
            }
        });

        binding.playlist2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle playlist 2 click event
            }
        });

        binding.playlist3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle playlist 3 click event
            }
        });

        // Handle bottom navigation
        binding.navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Home navigation
            }
        });

        binding.navSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Search navigation
            }
        });

        binding.navMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Music navigation
            }
        });

        binding.navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Profile navigation
            }
        });
    }
}
