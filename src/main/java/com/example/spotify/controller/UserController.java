package com.example.spotify.controller;

import com.example.spotify.dto.ChangePasswordRequest;
import com.example.spotify.dto.UpdateUsernameRequest;
import com.example.spotify.dto.UserDto;
import com.example.spotify.entity.Status;
import com.example.spotify.entity.User;
import com.example.spotify.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PutMapping("/update-username")
    public ResponseEntity<?> updateUsername(@RequestBody UpdateUsernameRequest request) {
        if (request.getUserId() == null || request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "User ID and username are required"));
        }

        String newUsername = request.getUsername().trim();

        Optional<User> optionalUser = userRepository.findById(request.getUserId());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }

        if (userRepository.existsByUsername(newUsername)) {
            User existing = optionalUser.get();
            if (!existing.getUsername().equals(newUsername)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Username already taken"));
            }
        }

        User user = optionalUser.get();
        user.setUsername(newUsername);
        User saved = userRepository.save(user);

        UserDto dto = toDto(saved);
        return ResponseEntity.ok(Map.of("message", "Username updated", "user", dto));
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        if (request.getUserId() == null || request.getOldPassword() == null || request.getNewPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "All fields are required"));
        }

        String oldPassword = request.getOldPassword();
        String newPassword = request.getNewPassword();

        if (newPassword.trim().length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("message", "New password must be at least 6 characters"));
        }

        Optional<User> optionalUser = userRepository.findById(request.getUserId());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }

        User user = optionalUser.get();

        String storedHash = user.getPasswordHash();
        if (storedHash == null || !storedHash.startsWith("$2")) {
            logger.warn("User {} has non-BCrypt password hash", user.getEmail());
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid account password format"));
        }

        if (!passwordEncoder.matches(oldPassword, storedHash)) {
            return ResponseEntity.status(401).body(Map.of("message", "Current password is incorrect"));
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        logger.info("Password changed for user {}", user.getEmail());
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    /**
     * PUT /api/users/{userId}/activate
     * Chuyển status từ NEW → ACTIVE sau khi user hoàn thành chọn artist onboarding.
     * Trả về UserDto mới để Flutter update AuthProvider.
     */
    @PutMapping("/{userId}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long userId) {
        if (userId == null || userId <= 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid userId"));
        }

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }

        User user = optionalUser.get();

        if (user.getStatus() == Status.BANNED) {
            return ResponseEntity.status(403).body(Map.of("message", "Account is banned"));
        }

        if (user.getStatus() == Status.ACTIVE) {
            return ResponseEntity.ok(Map.of("message", "Already active", "user", toDto(user)));
        }

        user.setStatus(Status.ACTIVE);
        User saved = userRepository.save(user);

        logger.info("User {} activated after onboarding", saved.getEmail());
        return ResponseEntity.ok(Map.of("message", "Account activated", "user", toDto(saved)));
    }

    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private UserDto toDto(User user) {
        String createdAt = user.getCreatedAt() != null
                ? user.getCreatedAt().format(DT_FMT)
                : null;
        return new UserDto(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().toString(),
                user.getStatus().toString(),
                user.getIsPremium(),
                createdAt
        );
    }
}
