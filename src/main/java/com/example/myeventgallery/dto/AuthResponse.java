package com.example.myeventgallery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long customerId;
    private String name;
    private String email;
    
    public AuthResponse(String token, Long customerId, String name, String email) {
        this.token = token;
        this.customerId = customerId;
        this.name = name;
        this.email = email;
    }
}
