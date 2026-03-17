package com.example.spotify.controller;

import com.example.spotify.dto.LoginRequest;
import com.example.spotify.dto.RegisterRequest;
import com.example.spotify.entity.Role;
import com.example.spotify.entity.Status;
import com.example.spotify.entity.User;
import com.example.spotify.repository.UserRepository;
import com.example.spotify.service.AuthService;
import com.example.spotify.service.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
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

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

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

        }else{
            user = optionalUser.get();
        }

        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(
                Map.of(
                        "token", token,
                        "email", email,
                        "username", name
                )
        );
    }
}
