package com.example.eventphoto.controller;

import com.example.eventphoto.dto.*;
import com.example.eventphoto.model.Customer;
import com.example.eventphoto.security.JwtPrincipal;
import com.example.eventphoto.service.CustomerService;
import com.example.eventphoto.service.EventService;
import com.example.eventphoto.service.GuestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final CustomerService customerService;
    private final GuestService guestService;

    @PostMapping
    public ResponseEntity<ApiResponse<EventResponse>> create(@Valid @RequestBody EventCreateRequest request) {
        JwtPrincipal principal = (JwtPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = customerService.findById(principal.getCustomerId());
        EventResponse created = eventService.createEvent(request, principal.getCustomerId(), customer);
        return ResponseEntity.ok(ApiResponse.success("Event created", created));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<EventResponse>>> myEvents() {
        JwtPrincipal principal = (JwtPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<EventResponse> list = eventService.getEventsByCustomerId(principal.getCustomerId());
        return ResponseEntity.ok(ApiResponse.success("OK", list));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<ApiResponse<EventResponse>> getById(@PathVariable Long eventId) {
        JwtPrincipal principal = (JwtPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EventResponse event = eventService.getEventResponse(eventId, principal.getCustomerId());
        return ResponseEntity.ok(ApiResponse.success("OK", event));
    }

    @GetMapping("/code/{eventCode}")
    public ResponseEntity<ApiResponse<EventPublicInfoDto>> getByCode(@PathVariable String eventCode) {
        EventPublicInfoDto dto = guestService.getEventPublicInfo(eventCode);
        return ResponseEntity.ok(ApiResponse.success("OK", dto));
    }

    @GetMapping("/{eventId}/grouped")
    public ResponseEntity<ApiResponse<EventImagesGroupedResponse>> getImagesGrouped(@PathVariable Long eventId) {
        JwtPrincipal principal = (JwtPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        eventService.ensureCustomerOwnsEvent(eventId, principal.getCustomerId());
        EventImagesGroupedResponse grouped = eventService.getImagesGroupedByGuest(eventId);
        return ResponseEntity.ok(ApiResponse.success("OK", grouped));
    }

    @GetMapping("/qr/{eventCode}")
    public ResponseEntity<byte[]> getQrCode(@PathVariable String eventCode) {
        eventService.getByEventCode(eventCode);
        byte[] qr = eventService.getQRCodeImage(eventCode);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"qr-" + eventCode + ".png\"")
                .body(qr);
    }
}
