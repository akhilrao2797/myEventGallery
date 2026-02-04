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
public class GuestDashboardResponse {
    private Long guestId;
    private String guestName;
    private List<GuestEventInfo> events;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GuestEventInfo {
        private Long eventId;
        private String eventName;
        private String eventCode;
        private String eventType;
        private LocalDateTime eventDate;
        private Integer uploadCount;
        private Boolean canDelete;
        private LocalDateTime deleteDeadline;
        private List<ImageResponse> uploadedImages;
    }
}
