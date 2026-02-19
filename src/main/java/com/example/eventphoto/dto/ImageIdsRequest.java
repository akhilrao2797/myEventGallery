package com.example.eventphoto.dto;

import lombok.Data;

import java.util.List;

@Data
public class ImageIdsRequest {
    private List<Long> imageIds;
}
