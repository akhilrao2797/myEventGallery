package com.example.eventphoto.dto;

import com.example.eventphoto.model.EventType;
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
public class EventResponse {
    private Long id;
    private String eventCode;
    private String name;
    private EventType eventType;
    private String description;
    private LocalDate eventDate;
    private LocalTime eventStartTime;
    private LocalTime eventEndTime;
    private String venue;
    private Integer expectedGuests;
    private String qrCodeUrl;
    private Long customerId;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Integer guestCount;
    private Integer totalImages;
}
