package com.example.myeventgallery.controller;

import com.example.myeventgallery.dto.ApiResponse;
import com.example.myeventgallery.dto.EventCreateRequest;
import com.example.myeventgallery.dto.EventResponse;
import com.example.myeventgallery.security.JwtUtil;
import com.example.myeventgallery.service.EventService;
import com.google.zxing.WriterException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(
            @Valid @RequestBody EventCreateRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long customerId = jwtUtil.extractCustomerId(token);
            
            EventResponse response = eventService.createEvent(request, customerId);
            return ResponseEntity.ok(ApiResponse.success("Event created successfully", response));
        } catch (WriterException | IOException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error generating QR code: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<EventResponse>>> getMyEvents(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long customerId = jwtUtil.extractCustomerId(token);
            
            List<EventResponse> events = eventService.getCustomerEvents(customerId);
            return ResponseEntity.ok(ApiResponse.success("Events retrieved successfully", events));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{eventId}")
    public ResponseEntity<ApiResponse<EventResponse>> getEvent(
            @PathVariable Long eventId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long customerId = jwtUtil.extractCustomerId(token);
            
            EventResponse event = eventService.getEventById(eventId, customerId);
            return ResponseEntity.ok(ApiResponse.success("Event retrieved successfully", event));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/qr/{eventCode}")
    public ResponseEntity<byte[]> getQRCode(@PathVariable String eventCode) {
        try {
            byte[] qrCode = eventService.getQRCode(eventCode);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(qrCode);
        } catch (WriterException | IOException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
