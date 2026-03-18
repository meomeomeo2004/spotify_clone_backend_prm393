package com.example.spotify.controller;

import com.example.spotify.dto.LoginRequest;
import com.example.spotify.dto.RegisterRequest;
import com.example.spotify.dto.AuthResponse;
import com.example.spotify.dto.UserDto;
import com.example.spotify.entity.Role;
import com.example.spotify.entity.Status;
import com.example.spotify.entity.User;
import com.example.spotify.repository.UserRepository;
import com.example.spotify.service.AuthService;
import com.example.spotify.service.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthService authService, 
                         UserRepository userRepository,
                         JwtService jwtService,
                         PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        if (response.getToken() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        if (response.getToken() == null) {
            String message = response.getMessage() != null ? response.getMessage() : "";
            if ("User not found".equalsIgnoreCase(message)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/google")
    public ResponseEntity<?> loginGoogle(@RequestBody Map<String, String> body) {

        String accessToken = body.get("accessToken");

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response =
                restTemplate.exchange(
                        "https://www.googleapis.com/oauth2/v3/userinfo",
                        HttpMethod.GET,
                        entity,
                        Map.class
                );

        Map<String, Object> googleUser = response.getBody();

        String email = (String) googleUser.get("email");
        String name = (String) googleUser.get("name");

        Optional<User> optionalUser = userRepository.findByEmail(email);

        User user;

        if(optionalUser.isEmpty()){
            user = new User();
            user.setEmail(email);
            user.setUsername(name);
            user.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));
            user.setRole(Role.USER);
            user.setStatus(Status.ACTIVE);
            user.setIsPremium(false);

            userRepository.save(user);
        } else {
            user = optionalUser.get();
        }

        String token = jwtService.generateToken(user);
        UserDto userDto = convertToUserDto(user);

        return ResponseEntity.ok(new AuthResponse(token, userDto));
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