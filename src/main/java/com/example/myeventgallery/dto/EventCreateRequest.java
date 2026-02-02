package com.example.myeventgallery.dto;

import com.example.myeventgallery.model.EventType;
import com.example.myeventgallery.model.PackageType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EventCreateRequest {
    
    @NotBlank(message = "Event name is required")
    private String name;
    
    @NotNull(message = "Event type is required")
    private EventType eventType;
    
    private String description;
    
    @NotNull(message = "Event date is required")
    @Future(message = "Event date must be in the future")
    private LocalDate eventDate;
    
    private String venue;
    
    private Integer expectedGuests;
    
    @NotNull(message = "Package type is required")
    private PackageType packageType;
}
