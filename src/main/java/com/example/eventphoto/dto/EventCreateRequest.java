package com.example.eventphoto.dto;

import com.example.eventphoto.model.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class EventCreateRequest {
    @NotBlank(message = "Event name is required")
    private String name;

    @NotNull
    private EventType eventType;

    private String description;

    @NotNull
    private LocalDate eventDate;

    private LocalTime eventStartTime;
    private LocalTime eventEndTime;

    private String venue;
    private Integer expectedGuests;
}
