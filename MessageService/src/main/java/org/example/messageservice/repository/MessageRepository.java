package org.example.messageservice.repository;

import org.example.messageservice.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

    @Repository
    public interface MessageRepository extends JpaRepository<Message, UUID> {


        @Query("SELECT m FROM Message m WHERE m.channelID = :channelId AND m.isDeleted = false ORDER BY m.createdAt DESC")
        Page<Message> findByChannelIdAndIsDeletedFalseOrderByCreatedAtDesc(
                UUID channelId, Pageable pageable);
        
        @Query("SELECT m FROM Message m WHERE m.replyToMessageId = :replyToMessageId AND m.isDeleted = false ORDER BY m.createdAt ASC")
        List<Message> findByReplyToMessageIdAndIsDeletedFalseOrderByCreatedAtAsc(
                UUID replyToMessageId);
        @Query("SELECT COUNT(m) FROM Message m WHERE m.channelID = :channelId AND m.isDeleted = false")
        long countByChannelIdAndIsDeletedFalse(UUID channelId);



    }

