package com.example.soundsenseapp.data.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class ChatViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<ChatMessage>> messages = new MutableLiveData<>(new ArrayList<>());

    public LiveData<ArrayList<ChatMessage>> getMessages() {
        return messages;
    }

    public void addMessage(ChatMessage message) {
        ArrayList<ChatMessage> currentMessages = messages.getValue();
        if (currentMessages != null) {
            currentMessages.add(message);
            messages.setValue(currentMessages);
        }
    }
}


