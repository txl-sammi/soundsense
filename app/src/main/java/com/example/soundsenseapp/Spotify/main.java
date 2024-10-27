package com.example.soundsenseapp.Spotify;

import java.util.ArrayList;

public class main {
    public static void main(String[] args) throws Exception {
        SpotifyAPI test = new SpotifyAPI();
        // 50 200
        // 100
        ArrayList<Playlist> playlists = DatabaseHelper.getAllPlaylists();
        for (Playlist playlist: playlists){
            System.out.println(playlist.toString());
            System.out.println(playlist.creationDate);

        }

//        ArrayList<SongFormat> res =  test.getSongsByTempo("pop", 90, 100, 5);
//        DatabaseHelper.savePlaylist("My Playlist", res);
        ArrayList<SongFormat> retrievedSongs = DatabaseHelper.getPlaylistSongsById(1);
        for (SongFormat song : retrievedSongs) {
            System.out.println("Song: " + song.name + ", Artist: " + song.artist + song.spotifyLink+ song.albumCover);
        }
//        for(SongFormat song: res) {
//            System.out.println(song.name);
//            System.out.println(song.artist);
//            System.out.println(song.albumCover);
//            System.out.println(song.spotifyLink);
//            System.out.println("--------------------------");
//        }



    }
}
