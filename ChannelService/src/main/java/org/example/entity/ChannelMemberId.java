package org.example.entity;



import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ChannelMemberId implements Serializable {
    private UUID userId;
    private UUID channelId;

    public ChannelMemberId() {}

    public ChannelMemberId(UUID userId, UUID channelId) {
        this.userId = userId;
        this.channelId = channelId;
    }

    // Getters, setters, equals, and hashCode
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getChannelId() { return channelId; }
    public void setChannelId(UUID channelId) { this.channelId = channelId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChannelMemberId)) return false;
        ChannelMemberId that = (ChannelMemberId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(channelId, that.channelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, channelId);
    }
}
