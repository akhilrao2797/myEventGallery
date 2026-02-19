package com.example.eventphoto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GuestRegisterRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 100)
    private String name;

    @Size(max = 100)
    private String email;

    @Size(max = 20)
    private String phoneNumber;

    @Size(min = 6, max = 100)
    private String password;

    @NotBlank(message = "Event code is required")
    private String eventCode;
}
