package com.example.eventphoto.controller;

import com.example.eventphoto.dto.ApiResponse;
import com.example.eventphoto.dto.AlbumRequest;
import com.example.eventphoto.security.JwtPrincipal;
import com.example.eventphoto.service.AlbumPdfService;
import com.example.eventphoto.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/album")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumPdfService albumPdfService;
    private final EventService eventService;

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generatePdf(@RequestBody AlbumRequest request) throws IOException {
        JwtPrincipal principal = (JwtPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        eventService.ensureCustomerOwnsEvent(request.getEventId(), principal.getCustomerId());
        List<Long> imageIds = request.getImageIds() != null && !request.getImageIds().isEmpty()
                ? request.getImageIds()
                : List.of();
        if (imageIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        byte[] pdf = albumPdfService.generatePdf(imageIds);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"album-" + request.getEventId() + ".pdf\"")
                .body(pdf);
    }
}
