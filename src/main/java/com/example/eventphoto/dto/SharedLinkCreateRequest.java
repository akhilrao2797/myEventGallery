package com.example.eventphoto.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SharedLinkCreateRequest {
    @NotNull
    private Long eventId;
    @NotEmpty
    private List<Long> imageIds;
    private String folderName;
    private String password;
    private LocalDateTime expiresAt;
}
