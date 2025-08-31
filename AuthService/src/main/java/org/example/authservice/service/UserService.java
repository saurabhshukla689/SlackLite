package org.example.authservice.service;

import org.example.authservice.dto.AuthPayload;
import org.example.authservice.entity.User;
import org.example.authservice.repository.UserRepository;
import org.example.authservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);

        userRepository.save(user);
        return true;
    }

    public AuthPayload authenticate(String username, String password) {
        Optional<User> userOptional = findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                String token = jwtUtil.generateToken(username);
                return new AuthPayload(token, user);  // Returns both token AND user
            }
        }

        throw new RuntimeException("Invalid credentials");
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public List<User> findAll() {
        return userRepository.findAll();
    }

}
