package org.example.messageservice.dto;

import java.util.UUID;

public class SendMessageInput {
   // ID of the user sending the message
    private UUID channelId;
    private String content;
    private String messageType; // e.g., "TEXT", "IMAGE", "FILE"
    private String fileUrl; // URL to the file if messageType is "IMAGE" or "FILE"
    private UUID replyToMessageId; // ID of the message being replied to, if any


    public SendMessageInput() {}

    public UUID getChannelId() {
        return channelId;
    }

    public void setChannelId(UUID channelId) {
        this.channelId = channelId;
    }

    public SendMessageInput(UUID channelId, String content, String messageType, String fileUrl, UUID replyToMessageId) {
        this.channelId = channelId;
        this.content = content;
        this.messageType = messageType;
        this.fileUrl = fileUrl;
        this.replyToMessageId = replyToMessageId;
    }

    // Getters and Setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public UUID getReplyToMessageId() {
        return replyToMessageId;
    }

    public void setReplyToMessageId(UUID replyToMessageId) {
        this.replyToMessageId = replyToMessageId;
    }
}
