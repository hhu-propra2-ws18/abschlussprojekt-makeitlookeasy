package de.propra2.ausleiherino24.features.chat;

import lombok.Data;

@Data
public class ChatMessage {

    private MessageType type;
    private String content;
    private String sender;
    private String receiver;
    private String timestamp;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}

