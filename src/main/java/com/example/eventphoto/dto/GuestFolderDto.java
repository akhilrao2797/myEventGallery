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
public class GuestFolderDto {
    private Long guestId;
    private String guestName;
    private String guestEmail;
    private Integer imageCount;
    private List<ImageResponse> images;
}
