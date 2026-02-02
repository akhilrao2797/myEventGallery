package com.example.myeventgallery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestResponse {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private Integer uploadCount;
    private LocalDateTime createdAt;
}
