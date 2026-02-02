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
public class ImageResponse {
    private Long id;
    private String fileName;
    private String originalFileName;
    private String s3Url;
    private String thumbnailUrl;
    private Double fileSizeMB;
    private String contentType;
    private String guestName;
    private LocalDateTime uploadedAt;
}
