package com.example.soundsenseapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import com.example.soundsenseapp.data.util.ChatMessage;

public class MusicBotFragment extends Fragment {

    private ArrayList<ChatMessage> messageList;
    private ChatAdapter chatAdapter;
    private EditText inputBox;
    private RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_bot, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Music Bot");

        inputBox = view.findViewById(R.id.inputBox);
        Button sendButton = view.findViewById(R.id.sendButton);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        RecyclerView chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRecyclerView.setAdapter(chatAdapter);

        requestQueue = Volley.newRequestQueue(requireContext());

        sendButton.setOnClickListener(v -> {
            String userInput = inputBox.getText().toString();
            if (userInput.isEmpty()) {
                Toast.makeText(getContext(), "Input cannot be empty!", Toast.LENGTH_SHORT).show();
            } else {
                messageList.add(new ChatMessage(userInput, 0));
                chatAdapter.notifyItemInserted(messageList.size() - 1);
                inputBox.setText("");
                sendQuestion(userInput);
            }
        });

        return view;
    }

    private void sendQuestion(String question) {
        String url = "https://api.openai.com/v1/chat/completions";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("model", "gpt-3.5-turbo");

            JSONArray messagesArray = new JSONArray();
            JSONObject messageObject = new JSONObject();
            messageObject.put("role", "user");
            messageObject.put("content", question);
            messagesArray.put(messageObject);

            jsonObject.put("messages", messagesArray);
            jsonObject.put("max_tokens", 300);
            jsonObject.put("temperature", 0.7);  // 温度控制输出的随机性
        } catch (Exception e) {
            Log.e("ChatFragment", "Error creating JSON object: " + e.getMessage());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                response -> {
                    try {
                        String responseMsg = response.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");

                        messageList.add(new ChatMessage(responseMsg.trim(), 1));
                        chatAdapter.notifyItemInserted(messageList.size() - 1);
                    } catch (Exception e) {
                        Log.e("ChatFragment", "Error parsing response: " + e.getMessage());
                    }
                },
                error -> {
                    if (error.networkResponse != null) {
                        String errorMsg = new String(error.networkResponse.data);
                        Log.e("ChatFragment", "Error: " + errorMsg);
                    } else {
                        Log.e("ChatFragment", "Error: " + error.getMessage());
                    }
                }
        ) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.HashMap<String, String> headers = new java.util.HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer ");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

}