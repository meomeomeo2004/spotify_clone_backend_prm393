package com.example.spotify.service;
import com.example.spotify.dto.LoginRequest;
import com.example.spotify.dto.RegisterRequest;
import com.example.spotify.dto.AuthResponse;
import com.example.spotify.dto.UserDto;
import com.example.spotify.entity.Role;
import com.example.spotify.entity.Status;
import com.example.spotify.entity.User;
import com.example.spotify.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail() != null ? request.getEmail().trim() : "";
        String username = request.getUsername() != null ? request.getUsername().trim() : "";
        String password = request.getPassword() != null ? request.getPassword() : "";

        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            return new AuthResponse(null, null, "Username, email and password are required");
        }

        if (userRepository.existsByEmail(email)) {
            return new AuthResponse(null, null, "Email already exists");
        }

        if (userRepository.existsByUsername(username)) {
            return new AuthResponse(null, null, "Username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(Role.USER);
        user.setStatus(Status.NEW);
        user.setIsPremium(false);

        User savedUser = userRepository.save(user);
        
        String token = jwtService.generateToken(savedUser);
        UserDto userDto = convertToUserDto(savedUser);

        return new AuthResponse(token, userDto);
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail() != null ? request.getEmail().trim() : "";
        String password = request.getPassword() != null ? request.getPassword() : "";

        if (email.isEmpty() || password.isEmpty()) {
            return new AuthResponse(null, null, "Email and password are required");
        }

        User user = userRepository
                .findByEmail(email)
                .orElse(null);
        if (user == null) {
            return new AuthResponse(null, null, "User not found");
        }
        if (user.getStatus() == Status.BANNED) {
            return new AuthResponse(null, null, "Account banned");
        }

        String storedHash = user.getPasswordHash();
        if (storedHash == null || !storedHash.startsWith("$2")) {
            logger.warn("User {} has non-BCrypt password hash format", user.getEmail());
            return new AuthResponse(null, null, "Invalid account password format");
        }

        boolean passwordMatched = passwordEncoder.matches(password, storedHash);
        logger.info("Login attempt for {}. passwordMatched={}", email, passwordMatched);
        if (!passwordMatched) {
            return new AuthResponse(null, null, "Wrong password");
        }

        String token = jwtService.generateToken(user);
        UserDto userDto = convertToUserDto(user);

        return new AuthResponse(token, userDto);
        
    }

    private UserDto convertToUserDto(User user) {
        return new UserDto(
            user.getUserId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole().toString(),
            user.getStatus().toString(),
            user.getIsPremium()
        );
    }
}