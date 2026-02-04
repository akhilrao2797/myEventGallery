package com.example.myeventgallery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestLoginRequest {
    private String email;
    private String password;
    private String eventCode;
}
