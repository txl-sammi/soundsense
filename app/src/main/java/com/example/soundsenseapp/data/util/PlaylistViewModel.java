package com.example.soundsenseapp.data.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.soundsenseapp.Spotify.SongFormat;
import java.util.ArrayList;

public class PlaylistViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<SongFormat>> playlist = new MutableLiveData<>(new ArrayList<>());

    public LiveData<ArrayList<SongFormat>> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(ArrayList<SongFormat> songs) {
        playlist.postValue(songs);
    }
}

