package com.example.eventphoto.controller;

import com.example.eventphoto.dto.*;
import com.example.eventphoto.security.JwtPrincipal;
import com.example.eventphoto.service.GuestService;
import com.example.eventphoto.service.ImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/guest")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;
    private final ImageService imageService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<GuestResponse>> register(@Valid @RequestBody GuestRegisterRequest request) {
        GuestResponse response = guestService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Registered", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<GuestResponse>> login(@Valid @RequestBody GuestLoginRequest request) {
        GuestResponse response = guestService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @GetMapping("/event-info/{eventCode}")
    public ResponseEntity<ApiResponse<EventPublicInfoDto>> eventInfo(@PathVariable String eventCode) {
        EventPublicInfoDto dto = guestService.getEventPublicInfo(eventCode);
        return ResponseEntity.ok(ApiResponse.success("OK", dto));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<List<GuestEventWithImagesDto>>> dashboard() {
        JwtPrincipal principal = (JwtPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<GuestEventWithImagesDto> list = guestService.getDashboard(principal.getGuestId());
        return ResponseEntity.ok(ApiResponse.success("OK", list));
    }

    @PostMapping("/{eventId}/upload")
    public ResponseEntity<ApiResponse<List<ImageResponse>>> upload(
            @PathVariable Long eventId,
            @RequestParam("files") MultipartFile[] files) {
        JwtPrincipal principal = (JwtPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ImageResponse> uploaded = imageService.upload(eventId, principal.getGuestId(), files);
        return ResponseEntity.ok(ApiResponse.success("Uploaded " + uploaded.size() + " image(s)", uploaded));
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteOwnImage(@PathVariable Long imageId) {
        JwtPrincipal principal = (JwtPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        imageService.guestDeleteOwnImage(imageId, principal.getGuestId());
        return ResponseEntity.ok(ApiResponse.success("Deleted", null));
    }
}
