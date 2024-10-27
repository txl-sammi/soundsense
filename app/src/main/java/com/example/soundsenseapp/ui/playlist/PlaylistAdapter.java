package com.example.soundsenseapp.ui.playlist;// PlaylistAdapter.java
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundsenseapp.R;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    private Context context;
    private ArrayList<Playlist> playlistArrayList;

    public PlaylistAdapter(Context context, ArrayList<Playlist> playlistArrayList) {
        this.context = context;
        this.playlistArrayList = playlistArrayList;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = playlistArrayList.get(position);
        holder.nameTextView.setText(playlist.getName());
        holder.artistTextView.setText(playlist.getArtist());
        holder.albumCoverImageView.setImageResource(playlist.getAlbumCover());
        holder.spotifyLinkTextView.setText(playlist.getSpotifyLink());

        PlaylistItem item = playlist.get(position);

        // Attempt to load the album cover; use a placeholder if it fails
        int albumCoverId = context.getResources().getIdentifier(
                item.getAlbumCover(), "drawable", context.getPackageName()
        );

        if (albumCoverId != 0) {
            holder.albumCoverImageView.setImageResource(albumCoverId);
        } else {
            // Use a default placeholder image in case of missing drawable
            holder.albumCoverImageView.setImageResource(R.drawable.placeholder_album_cover);
        }

        holder.nameTextView.setText(item.getName());
        holder.artistTextView.setText(item.getArtist());
    }

    @Override
    public int getItemCount() {
        return playlistArrayList.size();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, artistTextView, spotifyLinkTextView;
        ImageView albumCoverImageView;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.playlist_name);
            artistTextView = itemView.findViewById(R.id.playlist_artist);
            spotifyLinkTextView = itemView.findViewById(R.id.spotify_link);
            albumCoverImageView = itemView.findViewById(R.id.album_cover);
        }
    }
}
