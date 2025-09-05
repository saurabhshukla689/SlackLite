package org.example.repository;

// src/main/java/com/slacklite/channelservice/repository/ChannelRepository.java

import org.example.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    // Find channels user is member of
    @Query("SELECT c FROM Channel c JOIN c.members m WHERE m.userId = :userId ORDER BY c.createdAt DESC")
    List<Channel> findChannelsByUserId(@Param("userId") UUID userId);

    // Find public channels
    @Query("SELECT c FROM Channel c WHERE c.isPrivate = false ORDER BY c.name ASC")
    List<Channel> findPublicChannels();

    // Find channel by name
    Optional<Channel> findByNameIgnoreCase(String name);

    // Check if channel exists and user has access
    @Query("SELECT c FROM Channel c LEFT JOIN c.members m " +
            "WHERE c.id = :channelId AND (c.isPrivate = false OR m.userId = :userId)")
    Optional<Channel> findChannelWithAccess(@Param("channelId") UUID channelId,
                                            @Param("userId") UUID userId);
}