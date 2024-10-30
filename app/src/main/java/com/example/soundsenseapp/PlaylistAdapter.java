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

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.SongViewHolder> {

    private ArrayList<SongFormat> songList;
    private OnSongClickListener onSongClickListener;

    public interface OnSongClickListener {
        void onSongClick(SongFormat song);
    }

    public PlaylistAdapter(ArrayList<SongFormat> songList, OnSongClickListener onSongClickListener) {
        this.songList = songList;
        this.onSongClickListener = onSongClickListener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        SongFormat song = songList.get(position);
        holder.bind(song, onSongClickListener);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        private TextView songName;
        private TextView artistName;
        private ImageView albumCover;
        private Button playButton;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.song_name);
            artistName = itemView.findViewById(R.id.artist_name);
            albumCover = itemView.findViewById(R.id.album_cover);
            playButton = itemView.findViewById(R.id.spotify_button);
        }

        public void bind(SongFormat song, OnSongClickListener listener) {
            songName.setText(song.getName());
            artistName.setText(song.getArtist());
            Picasso.get().load(song.getAlbumCover()).into(albumCover);
            playButton.setOnClickListener(v -> listener.onSongClick(song));
        }
    }
}

