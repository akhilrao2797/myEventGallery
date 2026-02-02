package com.example.myeventgallery.controller;

import com.example.myeventgallery.dto.ApiResponse;
import com.example.myeventgallery.dto.ImageResponse;
import com.example.myeventgallery.security.JwtUtil;
import com.example.myeventgallery.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    
    @Autowired
    private ImageService imageService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<List<ImageResponse>>> getEventImages(
            @PathVariable Long eventId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            List<ImageResponse> images = imageService.getEventImages(eventId);
            return ResponseEntity.ok(ApiResponse.success("Images retrieved successfully", images));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/event/{eventId}/paginated")
    public ResponseEntity<ApiResponse<Page<ImageResponse>>> getEventImagesPaginated(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("uploadedAt").descending());
            Page<ImageResponse> images = imageService.getEventImagesPaginated(eventId, pageable);
            return ResponseEntity.ok(ApiResponse.success("Images retrieved successfully", images));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(
            @PathVariable Long imageId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long customerId = jwtUtil.extractCustomerId(token);
            
            imageService.deleteImage(imageId, customerId);
            return ResponseEntity.ok(ApiResponse.success("Image deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
