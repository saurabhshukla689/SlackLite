package org.example.messageservice.dto;

import java.util.UUID;

public class DeleteMessageInput {
    public UUID getDeleterID() {
        return deleterID;
    }

    public void setDeleterID(UUID deleterID) {
        this.deleterID = deleterID;
    }

    public UUID getMessageID() {
        return messageID;
    }

    public void setMessageID(UUID messageID) {
        this.messageID = messageID;
    }

    private UUID deleterID;
    private UUID messageID;
    public DeleteMessageInput() {}
    public DeleteMessageInput(UUID deleterID, UUID messageID) {
        this.deleterID = deleterID;
        this.messageID = messageID;
    }
}
