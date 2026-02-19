package com.example.eventphoto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestResponse {
    private Long guestId;
    private String name;
    private String email;
    private Integer uploadCount;
    private String eventCode;
    private Long eventId;
}
