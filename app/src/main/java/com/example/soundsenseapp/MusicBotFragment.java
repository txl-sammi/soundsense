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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
        String url = "https://api.openai.com/v1/completions";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("model", "text-davinci-003");
            jsonObject.put("prompt", question);
            jsonObject.put("temperature", 0);
            jsonObject.put("max_tokens", 300);
            jsonObject.put("top_p", 1);
            jsonObject.put("frequency_penalty", 0.0);
            jsonObject.put("presence_penalty", 0.0);
        } catch (Exception e) {
            Log.e("ChatFragment", "Error creating JSON object: " + e.getMessage());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String responseMsg = response.getJSONArray("choices")
                                    .getJSONObject(0)
                                    .getString("text");
                            messageList.add(new ChatMessage(responseMsg.trim(), 1));
                            chatAdapter.notifyItemInserted(messageList.size() - 1);
                        } catch (Exception e) {
                            Log.e("ChatFragment", "Error parsing response: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ChatFragment", "Error: " + error.getMessage());
                    }
                }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.HashMap<String, String> headers = new java.util.HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer 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");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }
}
