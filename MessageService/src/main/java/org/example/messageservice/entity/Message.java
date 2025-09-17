package org.example.messageservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Message {

    public Message(){}

    public Message(String content, UUID senderUserID, UUID channelID, String messageType) {
        this.id = UUID.randomUUID();
        this.content = content;
        this.senderUserID = senderUserID;
        this.channelID = channelID;
        this.messageType = messageType;
    }

    @Id
    private UUID id;

    @Size(min = 1, max = 100, message = "Message content must be between 1 and 100 characters")
    @Column(nullable = false, length = 100,columnDefinition = "TEXT")
    @NotBlank(message = "Message content is required")
    private String content;

    @Column(name = "sender_user_id", nullable = false)
    private UUID senderUserID;

    @Column(name = "channel_id", nullable = false)
    private UUID channelID;

    @Column(name = "message_type")
    private String messageType;

    @Column(name ="file_url")
    private String fileUrl;

    @Column(name ="reply_to_message_id")
    private UUID replyToMessageId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;



    @OneToMany(mappedBy = "parentMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> replies = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_message_id", insertable = false, updatable = false)
    private Message parentMessage;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UUID getSenderUserID() {
        return senderUserID;
    }

    public void setSenderUserID(UUID senderUserID) {
        this.senderUserID = senderUserID;
    }

    public UUID getChannelID() {
        return channelID;
    }

    public void setChannelID(UUID channelID) {
        this.channelID = channelID;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public List<Message> getReplies() {
        return replies;
    }

    public void setReplies(List<Message> replies) {
        this.replies = replies;
    }

    public Message getParentMessage() {
        return parentMessage;
    }

    public void setParentMessage(Message parentMessage) {
        this.parentMessage = parentMessage;
    }
}


