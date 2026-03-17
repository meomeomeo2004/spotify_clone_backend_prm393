package com.example.spotify.service;
import com.example.spotify.dto.LoginRequest;
import com.example.spotify.dto.RegisterRequest;
import com.example.spotify.entity.Role;
import com.example.spotify.entity.Status;
import com.example.spotify.entity.User;
import com.example.spotify.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email already exists";
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            return "Username already exists";
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        user.setRole(Role.USER);
        user.setStatus(Status.ACTIVE);
        user.setIsPremium(false);

        userRepository.save(user);

        return "Register success";
    }

    public String login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() == Status.BANNED) {
            return "Account banned";
        }

        if (passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return "Login success";
        }

        return "Wrong password";
    }
}