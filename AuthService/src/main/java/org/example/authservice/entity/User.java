package org.example.authservice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;


@Entity
@Table(name = "users")
public class User {
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Id
    private UUID id;

    @Column(unique = true)
    private String username;

    private String password;

    private String email;

    // Constructors, getters, setters
    public User() {
        this.id = UUID.randomUUID();
    }

    public User(String username, String password,String email) {
        this();
        this.username = username;
        this.password = password;
        this.email = email;
    }


    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}