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
public class GuestEventWithImagesDto {
    private Long eventId;
    private String eventName;
    private EventPublicInfoDto eventInfo;
    private boolean canModify;
    private String modifyDeadlineMessage;
    private List<ImageResponse> images;
}
