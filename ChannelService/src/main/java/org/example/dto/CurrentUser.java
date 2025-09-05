package org.example.dto;


import java.util.List;
import java.util.UUID;

public class CurrentUser {

    private UUID id;
    private String username;
    private String email;
    private List<String> roles;

    // Constructors
    public CurrentUser() {}

    public CurrentUser(UUID id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = List.of("ROLE_USER"); // Default role
    }

    public CurrentUser(UUID id, String username, String email, List<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles != null ? roles : List.of("ROLE_USER");
    }

    public static CurrentUser fromAuthentication(String userId, String username, String email) {
        return new CurrentUser(
                UUID.fromString(userId),
                username,
                email
        );
    }

    public static CurrentUser fromAuthentication(String userId, String username, String email, List<String> roles) {
        return new CurrentUser(
                UUID.fromString(userId),
                username,
                email,
                roles
        );
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    // Utility methods
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    public boolean isAdmin() {
        return hasRole("ROLE_ADMIN") || hasRole("ADMIN");
    }

    public boolean isUser() {
        return hasRole("ROLE_USER") || hasRole("USER");
    }

    // toString for debugging
    @Override
    public String toString() {
        return "CurrentUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                '}';
    }

    // equals and hashCode (useful for testing)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CurrentUser that = (CurrentUser) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
