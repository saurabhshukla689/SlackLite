package org.example.service;


import org.example.dto.CreateChannelInput;
import org.example.dto.UpdateChannelInput;
import org.example.entity.Channel;
import org.example.entity.ChannelMember;
import org.example.channelservice.Role;
import org.example.repository.ChannelRepository;
import org.example.repository.ChannelMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional

public class ChannelService {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private ChannelMemberRepository memberRepository;
    
    public ChannelMember findById(UUID userId) {
        return memberRepository.findById(userId);
    }

    // Get all channels for user
    public List<Channel> getUserChannels(UUID userId) {
        return channelRepository.findChannelsByUserId(userId);
    }

    // Get single channel
    public Optional<Channel> getChannel(UUID channelId, UUID userId) {
        if (userId != null) {
            return channelRepository.findChannelWithAccess(channelId, userId);
        }
        return channelRepository.findById(channelId)
                .filter(channel -> !channel.getIsPrivate());
    }

    // Get public channels
    public List<Channel> getPublicChannels() {
        return channelRepository.findPublicChannels();
    }

    // Create channel
    public Channel createChannel(UUID creatorUser, CreateChannelInput input) {
        // Check if channel name already exists
        Optional<Channel> existingChannel = channelRepository.findByNameIgnoreCase(input.getName());
        if (existingChannel.isPresent()) {
            throw new RuntimeException("Channel name already exists");
        }

        Channel channel = new Channel(
                input.getName(),
                input.getDescription(),
                input.getIsPrivate(),
                creatorUser
        );
        Channel savedChannel = channelRepository.save(channel);

        // Create membership for creator
        ChannelMember channelMember = new ChannelMember(creatorUser, savedChannel.getId(), Role.ADMIN);
        channelMember.setChannel(savedChannel);
        ChannelMember savedMember = memberRepository.save(channelMember);




        // Create channel





        // Add creator as admin
//        ChannelMember adminMember = new ChannelMember(channelMember.getUserId(), savedChannel.getId(), Role.ADMIN);
//        memberRepository.save(adminMember);

        return savedChannel;
    }

    // Update channel
    public Channel updateChannel(UUID channelId, UUID userId, UpdateChannelInput input) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("Channel not found"));

        // Check permissions
        if (!canUserManageChannel(userId, channelId)) {
            throw new RuntimeException("Permission denied");
        }

        // Update fields
        if (input.getName() != null) {
            channel.setName(input.getName());
        }
        if (input.getDescription() != null) {
            channel.setDescription(input.getDescription());
        }
        if (input.getIsPrivate() != null) {
            channel.setIsPrivate(input.getIsPrivate());
        }

        return channelRepository.save(channel);
    }

    // Delete channel
    public boolean deleteChannel(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("Channel not found"));

        // Check permissions (creator or admin)
        if (!channel.getCreatedByUserId().equals(userId) && !isUserAdmin(userId, channelId)) {
            throw new RuntimeException("Permission denied");
        }

        // Delete all memberships first
        List<ChannelMember> members = memberRepository.findByChannelIdOrderByJoinedAtAsc(channelId);
        memberRepository.deleteAll(members);

        // Delete channel
        channelRepository.delete(channel);
        return true;
    }

    // Join channel
    public ChannelMember joinChannel(UUID userId, UUID channelId) {
        // Check if channel exists
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("Channel not found"));

        // Check if already member
        if (memberRepository.existsByUserIdAndChannelId(userId, channelId)) {
            throw new RuntimeException("Already a member");
        }

        // For private channels, user needs invitation (simplified for now)
        if (channel.getIsPrivate()) {
            throw new RuntimeException("Cannot join private channel");
        }

        // Create membership
        ChannelMember member = new ChannelMember(userId, channelId, Role.MEMBER);
        return memberRepository.save(member);
    }

    // Leave channel
    public boolean leaveChannel(UUID userId, UUID channelId) {
        ChannelMember member = memberRepository.findByUserIdAndChannelId(userId, channelId)
                .orElseThrow(() -> new RuntimeException("Not a member"));

        memberRepository.delete(member);
        return true;
    }

    // Get channel members
    public List<ChannelMember> getChannelMembers(UUID channelId) {
        return memberRepository.findByChannelIdOrderByJoinedAtAsc(channelId);
    }

    // Permission checks
    public boolean isUserMember(UUID userId, UUID channelId) {
        return memberRepository.existsByUserIdAndChannelId(userId, channelId);
    }

    public boolean isUserAdmin(UUID userId, UUID channelId) {
        return memberRepository.existsByUserIdAndChannelIdAndRole(userId, channelId, Role.ADMIN);
    }

    public boolean canUserJoinChannel(UUID userId, UUID channelId) {
        Channel channel = channelRepository.findById(channelId).orElse(null);
        if (channel == null) return false;

        // Public channels - anyone can join
        if (!channel.getIsPrivate()) return true;

        // Private channels - need invitation
        return false;
    }

    public boolean canUserSendMessage(UUID userId, UUID channelId) {
        return isUserMember(userId, channelId);
    }

    public boolean canUserManageChannel(UUID userId, UUID channelId) {
        Channel channel = channelRepository.findById(channelId).orElse(null);
        if (channel == null) return false;

        // Creator can manage
        if (channel.getCreatedByUserId().equals(userId)) return true;

        // Admin can manage
        return isUserAdmin(userId, channelId);
    }

    public long getMemberCount(UUID channelId) {
        return memberRepository.countByChannelId(channelId);
    }
}
