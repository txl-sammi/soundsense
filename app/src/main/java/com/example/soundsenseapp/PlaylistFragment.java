package com.example.soundsenseapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.soundsenseapp.Spotify.DatabaseHelper;
import com.example.soundsenseapp.Spotify.Playlist;
import java.util.ArrayList;

public class PlaylistFragment extends Fragment {

    private RecyclerView playlistRecyclerView;
    private AllPlaylistsAdapter allPlaylistsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        playlistRecyclerView = view.findViewById(R.id.savedPlaylistRecyclerView);
        playlistRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getContext());
        ArrayList<Playlist> playlists = DatabaseHelper.getAllPlaylists(databaseHelper);
        allPlaylistsAdapter = new AllPlaylistsAdapter(getContext(), playlists);
        playlistRecyclerView.setAdapter(allPlaylistsAdapter);
        return view;
    }
}
