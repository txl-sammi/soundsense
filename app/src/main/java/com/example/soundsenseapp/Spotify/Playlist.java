package com.example.soundsenseapp.Spotify;

import androidx.annotation.NonNull;

public class Playlist {
    public int id;
    public String name;
    public String creationDate;

    public Playlist(int id, String name, String creationDate) {
        this.id = id;
        this.name = name;
        this.creationDate = creationDate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCreationDate() {
        return creationDate;
    }

    @NonNull
    @Override
    public String toString() {
        return "Playlist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", creationDate='" + creationDate + '\'' +
                '}';
    }
}
