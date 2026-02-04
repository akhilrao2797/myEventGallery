package com.example.myeventgallery.controller;

import com.example.myeventgallery.dto.ApiResponse;
import com.example.myeventgallery.dto.AuthResponse;
import com.example.myeventgallery.dto.GuestDashboardResponse;
import com.example.myeventgallery.dto.GuestLoginRequest;
import com.example.myeventgallery.security.JwtUtil;
import com.example.myeventgallery.service.GuestAuthService;
import com.example.myeventgallery.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guest")
public class GuestAuthController {
    
    @Autowired
    private GuestAuthService guestAuthService;
    
    @Autowired
    private ImageService imageService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> guestLogin(@RequestBody GuestLoginRequest request) {
        try {
            AuthResponse response = guestAuthService.guestLogin(request);
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<GuestDashboardResponse>> getGuestDashboard(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long guestId = jwtUtil.extractGuestId(token);
            
            GuestDashboardResponse dashboard = guestAuthService.getGuestDashboard(guestId);
            return ResponseEntity.ok(ApiResponse.success("Dashboard retrieved successfully", dashboard));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/image/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteGuestImage(
            @PathVariable Long imageId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long guestId = jwtUtil.extractGuestId(token);
            
            guestAuthService.deleteGuestImage(guestId, imageId);
            return ResponseEntity.ok(ApiResponse.success("Image deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
