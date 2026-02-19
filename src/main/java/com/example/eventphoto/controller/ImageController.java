package com.example.eventphoto.controller;

import com.example.eventphoto.dto.*;
import com.example.eventphoto.security.JwtPrincipal;
import com.example.eventphoto.service.EventService;
import com.example.eventphoto.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/events/{eventId}/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
    private final EventService eventService;

    @PostMapping("/bulk-delete")
    public ResponseEntity<ApiResponse<Void>> bulkDelete(
            @PathVariable Long eventId,
            @RequestBody ImageIdsRequest request) {
        JwtPrincipal principal = (JwtPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        eventService.ensureCustomerOwnsEvent(eventId, principal.getCustomerId());
        imageService.bulkDelete(request.getImageIds(), principal.getCustomerId());
        return ResponseEntity.ok(ApiResponse.success("Deleted", null));
    }

    @PostMapping("/download-zip")
    public ResponseEntity<byte[]> downloadZip(
            @PathVariable Long eventId,
            @RequestBody ImageIdsRequest request) throws IOException {
        JwtPrincipal principal = (JwtPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        eventService.ensureCustomerOwnsEvent(eventId, principal.getCustomerId());
        byte[] zip = imageService.downloadAsZip(request.getImageIds(), principal.getCustomerId());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"event-" + eventId + "-photos.zip\"")
                .body(zip);
    }
}
