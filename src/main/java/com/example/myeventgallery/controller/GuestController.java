package com.example.myeventgallery.controller;

import com.example.myeventgallery.dto.ApiResponse;
import com.example.myeventgallery.dto.GuestRegistrationRequest;
import com.example.myeventgallery.dto.GuestResponse;
import com.example.myeventgallery.dto.ImageResponse;
import com.example.myeventgallery.model.Event;
import com.example.myeventgallery.model.Guest;
import com.example.myeventgallery.service.GuestService;
import com.example.myeventgallery.service.ImageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/guest")
public class GuestController {
    
    @Autowired
    private GuestService guestService;
    
    @Autowired
    private ImageService imageService;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<GuestResponse>> registerGuest(
            @Valid @RequestBody GuestRegistrationRequest request) {
        try {
            GuestResponse response = guestService.registerGuest(request);
            return ResponseEntity.ok(ApiResponse.success("Guest registered successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/{guestId}/upload")
    public ResponseEntity<ApiResponse<List<ImageResponse>>> uploadImages(
            @PathVariable Long guestId,
            @RequestParam("files") List<MultipartFile> files) {
        try {
            // Validate QR code timing
            Guest guest = guestService.getGuestById(guestId);
            Event event = guest.getEvent();
            
            if (!event.isQrCodeValid()) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime eventStart = (event.getEventStartTime() != null)
                    ? event.getEventDate().atTime(event.getEventStartTime())
                    : event.getEventDate().atStartOfDay();
                
                if (now.isBefore(eventStart)) {
                    return ResponseEntity.badRequest().body(
                        ApiResponse.error("Upload not allowed before event starts on " + eventStart)
                    );
                } else {
                    return ResponseEntity.badRequest().body(
                        ApiResponse.error("Upload window closed. QR code expired on " + event.getQrValidUntil())
                    );
                }
            }
            
            List<ImageResponse> images = imageService.uploadImages(files, guestId);
            return ResponseEntity.ok(ApiResponse.success("Images uploaded successfully", images));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error uploading files: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
