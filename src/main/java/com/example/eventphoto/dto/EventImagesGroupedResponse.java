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
public class EventImagesGroupedResponse {
    private Long eventId;
    private String eventName;
    private Integer totalImages;
    private List<GuestFolderDto> guestFolders;
}
