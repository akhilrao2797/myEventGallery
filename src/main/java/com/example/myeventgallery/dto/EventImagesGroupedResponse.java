package com.example.myeventgallery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventImagesGroupedResponse {
    private Long eventId;
    private String eventName;
    private Integer totalImages;
    private Integer totalGuests;
    private Map<String, GuestImagesGroup> guestGroups;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GuestImagesGroup {
        private Long guestId;
        private String guestName;
        private String guestEmail;
        private Integer imageCount;
        private java.util.List<ImageResponse> images;
    }
}
