package com.example.soundsenseapp.ui.playlist;

// com.example.soundsenseapp.ui.playlist.Playlist.java
public class Playlist {
    private String name;
    private String artist;
    private int albumCover;
    private String spotifyLink;

    public Playlist(String name, String artist, int albumCover, String spotifyLink) {
        this.name = name;
        this.artist = artist;
        this.albumCover = albumCover;
        this.spotifyLink = spotifyLink;
    }

    // Getters
    public String getName() { return name; }
    public String getArtist() { return artist; }
    public int getAlbumCover() { return albumCover; }
    public String getSpotifyLink() { return spotifyLink; }
}
