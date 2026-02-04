package com.example.myeventgallery.dto;

import com.example.myeventgallery.model.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminEventResponse {
    private Long id;
    private String eventCode;
    private String name;
    private EventType eventType;
    private LocalDate eventDate;
    private LocalTime eventStartTime;
    private LocalTime eventEndTime;
    private String venue;
    private Integer expectedGuests;
    private String qrCodeUrl;
    
    // Customer details
    private Long customerId;
    private String customerName;
    private String customerEmail;
    
    // Package details
    private String packageName;
    private Double packagePrice;
    
    // Stats
    private Integer guestCount;
    private Long totalUploads;
    private Double totalSizeMB;
    private Boolean isActive;
    private Boolean isExpired;
    private LocalDateTime qrValidUntil;
    private Boolean qrCodeValid;
    
    private LocalDateTime createdAt;
}
