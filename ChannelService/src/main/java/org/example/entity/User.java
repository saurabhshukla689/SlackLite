package org.example.entity;

import java.util.UUID;

public class User {
    private Long id;

    // Only include fields that you reference in your Channel service
    // Don't include @Entity, @Table, or other JPA annotations

    public User() {}

    public User(Long id) {
        this.id = id;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Add any other fields you need in Channel service
    // For example, if you display username in channels:
    // private String username;
    // public String getUsername() { return username; }
    // public void setUsername(String username) { this.username = username; }
}