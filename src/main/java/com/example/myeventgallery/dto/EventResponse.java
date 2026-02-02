package com.example.myeventgallery.dto;

import com.example.myeventgallery.model.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private Long id;
    private String eventCode;
    private String name;
    private EventType eventType;
    private String description;
    private LocalDate eventDate;
    private String venue;
    private Integer expectedGuests;
    private String qrCodeUrl;
    private String packageName;
    private Boolean isActive;
    private Boolean isExpired;
    private LocalDate expiryDate;
    private Integer totalUploads;
    private Double totalSizeMB;
    private Integer guestCount;
    private LocalDateTime createdAt;
}
