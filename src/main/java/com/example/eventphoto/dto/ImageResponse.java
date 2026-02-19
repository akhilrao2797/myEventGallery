package com.example.eventphoto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse {
    private Long id;
    private String fileName;
    private String originalFileName;
    private String storageUrl;
    private Double fileSizeMb;
    private String contentType;
    private Long eventId;
    private Long guestId;
    private String guestName;
    private LocalDateTime uploadedAt;
}
