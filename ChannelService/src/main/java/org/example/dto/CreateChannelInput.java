package org.example.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateChannelInput {

    @NotBlank(message = "Channel name is required")
    @Size(min = 1, max = 100, message = "Channel name must be between 1 and 100 characters")
    private String name;

    private String description;
    private Boolean isPrivate = false;

    // Constructors
    public CreateChannelInput() {}

    public CreateChannelInput(String name, String description, Boolean isPrivate) {
        this.name = name;
        this.description = description;
        this.isPrivate = isPrivate;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIsPrivate() { return isPrivate; }
    public void setIsPrivate(Boolean isPrivate) { this.isPrivate = isPrivate; }
}