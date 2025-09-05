package org.example.resolver;


import com.fasterxml.jackson.databind.JsonNode;
import io.jsonwebtoken.Claims;
import org.example.dto.CreateChannelInput;
import org.example.dto.CurrentUser;
import org.example.dto.UpdateChannelInput;
import org.example.entity.Channel;
import org.example.entity.ChannelMember;
import org.example.channelservice.Role;
import org.example.entity.User;
import org.example.service.ChannelService;
import org.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.federation.EntityMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class ChannelResolver {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ChannelService channelService;

    // ===== QUERIES =====

    @EntityMapping
    public Channel channel(@Argument String id) {
        UUID channelId = UUID.fromString(id);
        Optional<Channel> channel = channelService.getChannel(channelId, null);
        return channel.orElse(null);
    }

    @EntityMapping
    public User user(@Argument String id) {
        System.out.println("🔗 Channel Service: Creating User reference for id: " + id);
        // This is just a reference for federation - don't fetch from database
        // The actual User data will be resolved by the User service
        User user = new User();
        user.setId(Long.parseLong(id));
        return user;
    }


    @SchemaMapping(typeName = "User", field = "channels")
    public List<Channel> userChannels(User user) {
        return channelService.getUserChannels(UUID.fromString(String.valueOf(user.getId())));
    }
    @QueryMapping
    public List<Channel> channels(Authentication authentication) {
        CurrentUser currentUser = getCurrentUser(authentication);
        System.out.println("here !"+currentUser);
        return channelService.getUserChannels(currentUser.getId());
    }

    @QueryMapping
    public Channel channel(@Argument String id, Authentication authentication) {
        CurrentUser currentUser = getCurrentUser(authentication);
        UUID channelId = UUID.fromString(id);

        Optional<Channel> channel = channelService.getChannel(channelId, currentUser.getId());
        return channel.orElse(null);
    }

    @QueryMapping
    public List<Channel> publicChannels() {
        System.out.println("doobby!");
        return channelService.getPublicChannels();
    }

    @QueryMapping
    public List<ChannelMember> channelMembers(@Argument String channelId, Authentication authentication) {
        CurrentUser currentUser = getCurrentUser(authentication);
        UUID channelUUID = UUID.fromString(channelId);

        // Check if user has access to channel
        if (!channelService.isUserMember(currentUser.getId(), channelUUID)) {
            throw new RuntimeException("Access denied");
        }

        return channelService.getChannelMembers(channelUUID);
    }

    @QueryMapping
    public Boolean canUserJoinChannel(@Argument String channelId, Authentication authentication) {
        CurrentUser currentUser = getCurrentUser(authentication);
        UUID channelUUID = UUID.fromString(channelId);
        return channelService.canUserJoinChannel(currentUser.getId(), channelUUID);
    }

    @QueryMapping
    public Boolean canUserSendMessage(@Argument String channelId, Authentication authentication) {
        CurrentUser currentUser = getCurrentUser(authentication);
        UUID channelUUID = UUID.fromString(channelId);
        return channelService.canUserSendMessage(currentUser.getId(), channelUUID);
    }


    @MutationMapping
    public Channel createChannel(@Argument CreateChannelInput input, Authentication authentication) {
        CurrentUser currentUser = getCurrentUser(authentication);
        System.out.println("heress !"+currentUser.getId());
        return channelService.createChannel(currentUser.getId(), input);

    }

    @MutationMapping
    public Channel updateChannel(@Argument String id, @Argument UpdateChannelInput input,
                                 Authentication authentication) {
        CurrentUser currentUser = getCurrentUser(authentication);
        UUID channelId = UUID.fromString(id);
        return channelService.updateChannel(channelId, currentUser.getId(), input);
    }

    @MutationMapping
    public Boolean deleteChannel(@Argument String id, Authentication authentication) {
        CurrentUser currentUser = getCurrentUser(authentication);
        UUID channelId = UUID.fromString(id);
        return channelService.deleteChannel(channelId, currentUser.getId());
    }

    @MutationMapping
    public ChannelMember joinChannel(@Argument String channelId, Authentication authentication) {
        CurrentUser currentUser = getCurrentUser(authentication);
        UUID channelUUID = UUID.fromString(channelId);
        return channelService.joinChannel(currentUser.getId(), channelUUID);
    }

    @MutationMapping
    public Boolean leaveChannel(@Argument String channelId, Authentication authentication) {
        CurrentUser currentUser = getCurrentUser(authentication);
        UUID channelUUID = UUID.fromString(channelId);
        return channelService.leaveChannel(currentUser.getId(), channelUUID);
    }

    @MutationMapping
    public ChannelMember inviteToChannel(@Argument String channelId, @Argument String userId,
                                         Authentication authentication) {
        CurrentUser currentUser = getCurrentUser(authentication);
        UUID channelUUID = UUID.fromString(channelId);
        UUID userUUID = UUID.fromString(userId);

        // Check permissions
        if (!channelService.canUserManageChannel(currentUser.getId(), channelUUID)) {
            throw new RuntimeException("Permission denied");
        }

        return channelService.joinChannel(userUUID, channelUUID);
    }

    @MutationMapping
    public Boolean removeFromChannel(@Argument String channelId, @Argument String userId,
                                     Authentication authentication) {
        CurrentUser currentUser = getCurrentUser(authentication);
        UUID channelUUID = UUID.fromString(channelId);
        UUID userUUID = UUID.fromString(userId);

        if (!channelService.canUserManageChannel(currentUser.getId(), channelUUID)) {
            throw new RuntimeException("Permission denied");
        }

        return channelService.leaveChannel(userUUID, channelUUID);
    }

    // ===== SCHEMA MAPPINGS (Field Resolvers) =====

    @SchemaMapping
    public Integer memberCount(Channel channel) {
        return (int) channelService.getMemberCount(channel.getId());
    }

    @SchemaMapping
    public List<ChannelMember> members(Channel channel) {
        return channelService.getChannelMembers(channel.getId());
    }

    // ===== HELPER METHODS =====

    private CurrentUser getCurrentUser(Authentication authentication) {

        System.out.println("before exception");
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Authentication required");
        }


        // Extract user info from JWT token (you'll implement this based on your auth service)
        String userId = authentication.getName();
        Claims claims = jwtUtil.getClaimsFromToken(userId);
        UUID id = UUID.fromString((String) claims.get("id"));
        System.out.println("after exception11"+ authentication);
        System.out.println("after exception"+userId);// This depends on how you store user ID in JWT



        return new CurrentUser(
                id,
                (String) claims.get("username"),
                (String) claims.get("email"),
                null // You can add more user details here
        );
    }


}
