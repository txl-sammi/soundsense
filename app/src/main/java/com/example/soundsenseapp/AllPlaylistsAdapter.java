package com.example.soundsenseapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.soundsenseapp.Spotify.Playlist;

import java.util.ArrayList;

public class AllPlaylistsAdapter extends RecyclerView.Adapter<AllPlaylistsAdapter.ViewHolder> {

    private final ArrayList<Playlist> playlists;
    private final Context context;

    public AllPlaylistsAdapter(Context context, ArrayList<Playlist> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.playlistNameTextView.setText(playlist.getName());
        holder.creationDateTextView.setText(playlist.getCreationDate());

        // 设置按钮的点击事件
        holder.viewSongsButton.setOnClickListener(v -> {
            // 启动新Activity，传递播放列表ID
            Intent intent = new Intent(context, SongsActivity.class);
            intent.putExtra("PLAYLIST_ID", playlist.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView playlistNameTextView;
        TextView creationDateTextView;
        Button viewSongsButton; // 新增的按钮

        ViewHolder(View itemView) {
            super(itemView);
            playlistNameTextView = itemView.findViewById(R.id.playlist_name);
            creationDateTextView = itemView.findViewById(R.id.creation_date);
            viewSongsButton = itemView.findViewById(R.id.view_songs_button); // 绑定按钮
        }
    }
}


