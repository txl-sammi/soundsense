package com.example.soundsenseapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.soundsenseapp.Spotify.DatabaseHelper;
import com.example.soundsenseapp.Spotify.SongFormat;
import java.util.ArrayList;

public class SongsActivity extends AppCompatActivity {

    private RecyclerView songsRecyclerView;
    private SongsAdapter songsAdapter;
    private TextView playlistNameTextView;
    private int playlistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        playlistId = getIntent().getIntExtra("PLAYLIST_ID", -1);
        String playlistName = getIntent().getStringExtra("PLAYLIST_NAME");
        playlistNameTextView = findViewById(R.id.playlistTitle);
        playlistNameTextView.setText(playlistName);
        songsRecyclerView = findViewById(R.id.songsRecyclerView);
        songsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        ArrayList<SongFormat> songs = databaseHelper.getPlaylistSongsById(databaseHelper, playlistId);
        songsAdapter = new SongsAdapter(this, songs);
        songsRecyclerView.setAdapter(songsAdapter);
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
