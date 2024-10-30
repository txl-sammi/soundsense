package com.example.soundsenseapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.soundsenseapp.Spotify.SongFormat;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    private final ArrayList<SongFormat> playlist;
    private final OnSpotifyLinkClickListener linkClickListener;

    public interface OnSpotifyLinkClickListener {
        void onSpotifyLinkClick(String spotifyLink);
    }

    public PlaylistAdapter(ArrayList<SongFormat> playlist, OnSpotifyLinkClickListener listener) {
        this.playlist = playlist;
        this.linkClickListener = listener;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        SongFormat song = playlist.get(position);
        holder.songName.setText(song.getName());
        holder.artistName.setText(song.getArtist());
        Picasso.get().load(song.getAlbumCover()).into(holder.albumCover);
        holder.spotifyButton.setOnClickListener(v -> linkClickListener.onSpotifyLinkClick(song.getSpotifyLink()));
    }

    @Override
    public int getItemCount() {
        return playlist.size();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        TextView songName, artistName;
        ImageView albumCover;
        Button spotifyButton;

        public PlaylistViewHolder(View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.song_name);
            artistName = itemView.findViewById(R.id.artist_name);
            albumCover = itemView.findViewById(R.id.album_cover);
            spotifyButton = itemView.findViewById(R.id.spotify_button);
        }
    }
}

