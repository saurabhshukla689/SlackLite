package org.example.authservice.service;

import io.jsonwebtoken.Claims;
import org.example.authservice.dto.AuthPayload;
import org.example.authservice.entity.User;
import org.example.authservice.repository.UserRepository;
import org.example.authservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;



    @Autowired
    private PasswordEncoder passwordEncoder;
    public User save(User user) {
        return userRepository.save(user);
    }

    public boolean registerUser(String username, String password, String email) {
        if (userRepository.existsByUsername(username)) {
            return false; // User already exists
        }

        User user = new User(username, passwordEncoder.encode(password), email);
//        user.setUsername(username);
//        user.setPassword(passwordEncoder.encode(password));
//        user.setEmail(email);
        System.out.println("usersss"+ user.getId());

        userRepository.save(user);
        return true;
    }

    public AuthPayload authenticate(String username, String password) {
        Optional<User> userOptional = findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                String name = user.getUsername();
                String email = user.getEmail();
                String token = jwtUtil.generateToken(user.getId(),name,email);
                return new AuthPayload(token, user);  // Returns both token AND user
            }
        }

        throw new RuntimeException("Invalid credentials");
    }
    public Optional<User> findByUserId(UUID userId) {
        return userRepository.findById(userId);
    }
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public List<User> findAll() {
        return userRepository.findAll();
    }

}
