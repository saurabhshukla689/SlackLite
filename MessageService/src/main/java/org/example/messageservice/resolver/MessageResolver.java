package org.example.messageservice.resolver;

import io.jsonwebtoken.Claims;
import org.example.messageservice.dto.CurrentUser;
import org.example.messageservice.dto.DeleteMessageInput;
import org.example.messageservice.dto.SendMessageInput;
import org.example.messageservice.dto.UpdateMessageInput;
import org.example.messageservice.entity.Message;
import org.example.messageservice.service.MessageService;
import org.example.messageservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.federation.EntityMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class MessageResolver {

    @Autowired
    private MessageService messageService;

    @Autowired
    private JwtUtil jwtUtil;

    @EntityMapping
    public Message message(UUID id) {
        return messageService.getMessageById(id);
    }

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


    @MutationMapping
    public Message sendMessage(@Argument SendMessageInput input, Authentication authentication) {
        CurrentUser currentUser = getCurrentUser(authentication);
        UUID senderId = currentUser.getId();
        return messageService.createMessage(input, senderId,authentication.getName());
    }

    @MutationMapping
    public Message updateMessage(@Argument UpdateMessageInput input , Authentication authentication) {
        CurrentUser currentUser = getCurrentUser(authentication);
        String updatedMessage = input.getContent();

        if(!input.getUpdaterID().equals(currentUser.getId())){
            throw new IllegalArgumentException("You are not authorized to update this message");
        }
        return messageService.updateMessage(input);
    }

    @MutationMapping
    public boolean deleteMessage(@Argument DeleteMessageInput input, Authentication authentication) {
        CurrentUser currentUser = getCurrentUser(authentication);
        UUID deleterId = input.getDeleterID();
        if(!deleterId.equals(currentUser.getId())){
            throw new IllegalArgumentException("You are not authorized to delete this message");
        }
        return messageService.deleteMessage(input);
    }

}
