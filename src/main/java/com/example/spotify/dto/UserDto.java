package com.example.spotify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDto {
    private Long userId;
    private String username;
    private String email;
    private String role;
    private String status;
    private Boolean isPremium;
}