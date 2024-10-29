package com.example.soundsenseapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class MusicBotFragment extends Fragment {

    public MusicBotFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_bot, container, false);
        TextView textView = view.findViewById(R.id.fragment_music_bot);
        textView.setText("music bot");
        return view;
    }
}