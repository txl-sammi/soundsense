package com.example.soundsenseapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.soundsenseapp.Spotify.SongFormat;
import java.util.ArrayList;
import com.squareup.picasso.Picasso;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {

    private final ArrayList<SongFormat> songs;
    private final Context context;

    public SongsAdapter(Context context, ArrayList<SongFormat> songs) {
        this.context = context;
        this.songs = songs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SongFormat song = songs.get(position);
        holder.songNameTextView.setText(song.name);
        holder.artistTextView.setText(song.artist);
        Picasso.get().load(song.getAlbumCover()).into(holder.albumCoverImageView);

        holder.playButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(song.spotifyLink));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView songNameTextView;
        TextView artistTextView;
        ImageView albumCoverImageView;
        Button playButton;

        ViewHolder(View itemView) {
            super(itemView);
            songNameTextView = itemView.findViewById(R.id.song_name);
            artistTextView = itemView.findViewById(R.id.artist_name);
            albumCoverImageView = itemView.findViewById(R.id.album_cover);
            playButton = itemView.findViewById(R.id.spotify_button);
        }
    }
}

