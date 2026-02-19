package com.example.eventphoto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedLinkResponse {
    private Long id;
    private String shareCode;
    private String shareUrl;
    private String url;
    private String folderName;
    private boolean hasPassword;
    private boolean hasExpiry;
    private java.time.LocalDateTime expiresAt;
    private Integer imageCount;
    private List<ImageResponse> images;
    private List<String> imageUrls;
}
