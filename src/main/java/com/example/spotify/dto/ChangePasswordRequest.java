package com.example.spotify.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    private Long userId;
    private String oldPassword;
    private String newPassword;
}
