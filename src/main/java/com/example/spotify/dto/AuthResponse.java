package com.example.spotify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UserDto user;
    private String message;

    public AuthResponse(String token, UserDto user) {
        this.token = token;
        this.user = user;
        this.message = "Authentication successful";
    }
}