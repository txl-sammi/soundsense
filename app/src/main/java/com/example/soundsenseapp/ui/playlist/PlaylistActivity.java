package com.example.soundsenseapp.ui.playlist;// MainActivity.java
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundsenseapp.R;
import com.example.soundsenseapp.ui.playlist.Playlist;
import com.example.soundsenseapp.ui.playlist.PlaylistAdapter;

import java.util.ArrayList;

public class PlaylistActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        ArrayList<Playlist> playlists = new ArrayList<>();
        playlists.add(new Playlist("Song 1", "Artist 1", R.drawable.album_cover_1, "https://open.spotify.com/track/1"));
        playlists.add(new Playlist("Song 2", "Artist 2", R.drawable.album_cover_2, "https://open.spotify.com/track/2"));
        playlists.add(new Playlist("Song 3", "Artist 3", R.drawable.album_cover_3, "https://open.spotify.com/track/3"));

        RecyclerView recyclerView = findViewById(R.id.playlist_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new PlaylistAdapter(this, playlists));
    }
}
