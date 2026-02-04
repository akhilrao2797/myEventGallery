package com.example.myeventgallery.dto;

import com.example.myeventgallery.model.AdminRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminRegistrationRequest {
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    @NotNull(message = "Role is required")
    private AdminRole role;
    
    // Super admin secret key for validation (only super admins can create other admins)
    private String superAdminKey;
}
