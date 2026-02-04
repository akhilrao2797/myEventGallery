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
public class CreateSharedFolderRequest {
    private String folderName;
    private Long eventId;
    private List<Long> imageIds;
    private String accessPassword;
    private LocalDateTime expiryDate;
}
