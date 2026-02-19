package com.example.eventphoto.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AlbumRequest {
    @NotNull
    private Long eventId;
    private List<Long> imageIds;
}
