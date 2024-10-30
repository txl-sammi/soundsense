package com.example.soundsenseapp.Spotify;

public class SongFormat {
    public String name;
    public String artist;
    public String albumCover;
    public String spotifyLink;

    public SongFormat(String name, String artist, String albumCover, String spotifyLink) {
        this.name = name;
        this.artist = artist;
        this.albumCover = albumCover;
        this.spotifyLink = spotifyLink;
    }

    public String getName() {
        return this.name;
    }

    public String getArtist() {
        return this.artist;
    }

    public String getAlbumCover() {
        return albumCover;
    }

    public String getSpotifyLink() {
        return spotifyLink;
    }
}