package org.example.messageservice.service;

import org.example.messageservice.dto.DeleteMessageInput;
import org.example.messageservice.dto.SendMessageInput;
import org.example.messageservice.dto.UpdateMessageInput;
import org.example.messageservice.entity.Message;
import org.example.messageservice.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    private WebClient webClient = WebClient.create("http://localhost:8080");


    private String channelGraphQLUrl = "http://localhost:8082/graphql";
    private boolean channelExists(UUID channelId,String token) {
        String query = "query { channels(id: \"" + channelId + "\") { id } }";

        // Create proper JSON object
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);

        try {
            String response = webClient.post()
                    .uri(channelGraphQLUrl)
                    .contentType(MediaType.APPLICATION_JSON)  // Use contentType() instead of header()
                    .header("Authorization", "Bearer " + token)
                    .bodyValue(requestBody)  // ✅ Pass Map instead of manual JSON string
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return response != null && !response.contains("\"data\":null") && !response.contains("\"errors\":");
        } catch (Exception e) {
            System.err.println("GraphQL request failed: " + e.getMessage());
            return false;
        }
    }

    private boolean userHasAccessToChannel(UUID userId, UUID channelId,String token) {
        String query = "query { canUserSendMessage(channelId: \"" + channelId + "\") }";

        try {
            String response = webClient.post()
                    .uri(channelGraphQLUrl)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .bodyValue("{\"query\":\"" + query + "\"}")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return response != null && response.contains("true");
        } catch (Exception e) {
            return false;
        }
    }



        public Message createMessage(SendMessageInput sendMessageInput, UUID userId,String token) {
            Message message = new Message(sendMessageInput.getContent(), userId, sendMessageInput.getChannelId(), sendMessageInput.getMessageType());
            Message returnMessage = null;
            if(!channelExists(sendMessageInput.getChannelId(),token)) {
                throw new RuntimeException("Channel doesnt exist");
            }

            if(!userHasAccessToChannel(userId, sendMessageInput.getChannelId(),token)) {
                throw new RuntimeException("User does not have access to this channel");
            }



            if (sendMessageInput.getFileUrl() != null) {
                message.setFileUrl(sendMessageInput.getFileUrl());
            }
            if (sendMessageInput.getReplyToMessageId() == null) { // this is a normal message , not a reply
            returnMessage= messageRepository.save(message);
            }


         else {
            // this is a reply to another message
            Message parentMessage = messageRepository.findById(sendMessageInput.getReplyToMessageId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent message not found"));

            message.setReplyToMessageId(parentMessage.getId());

            message.setParentMessage(parentMessage);

        }
            return returnMessage;
    }

    public Message updateMessage(UpdateMessageInput updateMessageInput) {
        Message message = messageRepository.findById(updateMessageInput.getMessageID())
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));

        if(updateMessageInput.getUpdaterID()!=message.getSenderUserID()){
            throw new IllegalArgumentException("You are not authorized to update this message");
        }

        message.setContent(updateMessageInput.getContent());
        return messageRepository.save(message);
    }

    public boolean deleteMessage(DeleteMessageInput updateMessageInput) {
        Message message = messageRepository.findById(updateMessageInput.getMessageID())
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));

        if(updateMessageInput.getDeleterID()!=message.getSenderUserID()){
            throw new IllegalArgumentException("You are not authorized to update this message");
        }


         messageRepository.delete(message);
         return true;
        }

    public Message getMessageById(UUID id) {
            return messageRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Message not found"));
    }
}
