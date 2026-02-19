package com.example.eventphoto.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GuestLoginRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String eventCode;
}
