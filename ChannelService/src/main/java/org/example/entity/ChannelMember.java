package org.example.entity;

import jakarta.persistence.*;
import org.example.channelservice.Role;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "channel_members", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "channel_id"}))
public class ChannelMember {
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Id
    private UUID id;


    @Column(name = "user_id", nullable = false)
    private UUID userId;


    @Column(name = "channel_id", nullable = false)
    private UUID channelId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.MEMBER;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @Column(name = "last_read_message_id")
    private UUID lastReadMessageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", insertable = false, updatable = false)
    private Channel channel;

    // Constructors
    public ChannelMember() {
    this.id = UUID.randomUUID();
    }

    public ChannelMember(UUID userId, UUID channelId, Role role) {
        this();
        this.userId = userId;
        this.channelId = channelId;
        this.role = role != null ? role : Role.MEMBER;
    }

    // Getters and Setters


    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getChannelId() { return channelId; }
    public void setChannelId(UUID channelId) { this.channelId = channelId; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }

    public UUID getLastReadMessageId() { return lastReadMessageId; }
    public void setLastReadMessageId(UUID lastReadMessageId) { this.lastReadMessageId = lastReadMessageId; }

    public Channel getChannel() { return channel; }
    public void setChannel(Channel channel) { this.channel = channel; }
}