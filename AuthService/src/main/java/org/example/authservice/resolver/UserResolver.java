package org.example.authservice.resolver;


import org.example.authservice.entity.User;
import org.example.authservice.dto.AuthPayload;
import org.example.authservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.federation.EntityMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class UserResolver {

    @Autowired
    private UserService userService;

    @QueryMapping
    public Optional<User> me(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        return userService.findByUsername(authentication.getName());
    }




    @EntityMapping
    public Optional<User> user(@Argument UUID id) {
        return userService.findByUserId(id);
    }

    @QueryMapping
    public List<User> users() {
        return userService.findAll();
    }

    @MutationMapping
    public AuthPayload login(@Argument String username, @Argument String password) {
        return userService.authenticate(username, password);
    }

    @MutationMapping
    public boolean register(@Argument String username, @Argument String password, @Argument String email) {
        return userService.registerUser(username, password, email);
    }

    @MutationMapping
    public Boolean logout() {
        // In stateless JWT authentication, logout is handled client-side
        // by removing the token from storage
        return true;
    }
}