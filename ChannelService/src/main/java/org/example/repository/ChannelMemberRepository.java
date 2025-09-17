package org.example.repository;


import org.example.entity.ChannelMember;
import org.example.entity.ChannelMemberId;
import org.example.channelservice.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChannelMemberRepository extends JpaRepository<ChannelMember, ChannelMemberId> {

    ChannelMember findById(UUID userId)   ;

    List<ChannelMember> findByChannelIdOrderByJoinedAtAsc(UUID channelId);

    Optional<ChannelMember> findByUserIdAndChannelId(UUID userId, UUID channelId);

    boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);

    boolean existsByUserIdAndChannelIdAndRole(UUID userId, UUID channelId, Role role);

    long countByChannelId(UUID channelId);

    @Query("SELECT cm FROM ChannelMember cm WHERE cm.userId = :userId AND cm.role = :role")
    List<ChannelMember> findByUserIdAndRole(@Param("userId") UUID userId, @Param("role") Role role);
}