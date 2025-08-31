package org.example.authservice.dto;

import org.example.authservice.entity.User;

public class AuthPayload {
    private String token;
    private User user;

    public AuthPayload(String token, User user) {
        this.token = token;
        this.user = user;
    }

    // Getters and setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
