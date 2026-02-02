package com.example.myeventgallery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GuestRegistrationRequest {
    
    @NotBlank(message = "Guest name is required")
    private String name;
    
    private String email;
    
    private String phoneNumber;
    
    @NotBlank(message = "Event code is required")
    private String eventCode;
}
