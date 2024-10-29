package com.example.soundsenseapp.data.util;

public class ChatMessage {
    private String text;
    private int type; // 0 for user message, 1 for bot response

    public ChatMessage(String text, int type) {
        this.text = text;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return text;
    }

    public String getAuthor() {
        if (type == 0) {
            return "You";
        } else {
            return "Bot";
        }
    }
}


