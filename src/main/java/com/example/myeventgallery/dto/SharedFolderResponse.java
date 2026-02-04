package com.example.myeventgallery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedFolderResponse {
    private Long id;
    private String folderName;
    private String shareCode;
    private String shareUrl;
    private Boolean hasPassword;
    private Integer imageCount;
    private LocalDateTime expiryDate;
    private Boolean isActive;
    private Integer downloadCount;
    private LocalDateTime createdAt;
    private List<ImageResponse> images;
}
