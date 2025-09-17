package org.example.messageservice.dto;

import java.util.UUID;

public class UpdateMessageInput {
    private String content;

    public UUID getUpdaterID() {
        return updaterID;
    }

    public void setUpdaterID(UUID updaterID) {
        this.updaterID = updaterID;
    }

    private UUID updaterID;

    public UUID getMessageID() {
        return messageID;
    }

    public void setMessageID(UUID messageID) {
        this.messageID = messageID;
    }

    private UUID messageID;

    public UpdateMessageInput() {}

    public UpdateMessageInput(UUID messageID,String content,UUID updaterID) {
        this.messageID = messageID;
        this.content = content;
        this.updaterID = updaterID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
