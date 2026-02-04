package com.example.myeventgallery.service;

import com.example.myeventgallery.dto.AuthResponse;
import com.example.myeventgallery.dto.GuestDashboardResponse;
import com.example.myeventgallery.dto.GuestLoginRequest;
import com.example.myeventgallery.dto.ImageResponse;
import com.example.myeventgallery.model.Event;
import com.example.myeventgallery.model.Guest;
import com.example.myeventgallery.model.Image;
import com.example.myeventgallery.repository.EventRepository;
import com.example.myeventgallery.repository.GuestRepository;
import com.example.myeventgallery.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GuestAuthService {
    
    @Autowired
    private GuestRepository guestRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Transactional
    public AuthResponse guestLogin(GuestLoginRequest request) {
        // Find guest by email and event code
        Guest guest = guestRepository.findByEmailAndEventEventCode(
                request.getEmail(), 
                request.getEventCode()
        ).orElseThrow(() -> new RuntimeException("Guest not found for this event"));
        
        // Verify password if set
        if (guest.getPassword() != null && !passwordEncoder.matches(request.getPassword(), guest.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        // Generate JWT token for guest
        String token = jwtUtil.generateGuestToken(guest.getEmail(), guest.getId());
        
        return new AuthResponse(token, guest.getId(), guest.getName(), guest.getEmail());
    }
    
    @Transactional
    public GuestDashboardResponse getGuestDashboard(Long guestId) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new RuntimeException("Guest not found"));
        
        // Get all events where this guest has uploaded photos
        List<Image> allImages = guest.getUploadedImages();
        
        // Group images by event
        Map<Event, List<Image>> imagesByEvent = allImages.stream()
                .collect(Collectors.groupingBy(Image::getEvent));
        
        // Build event info list
        List<GuestDashboardResponse.GuestEventInfo> eventInfos = new ArrayList<>();
        
        for (Map.Entry<Event, List<Image>> entry : imagesByEvent.entrySet()) {
            Event event = entry.getKey();
            List<Image> eventImages = entry.getValue();
            
            // Calculate delete deadline (event date + 1 day)
            LocalDateTime deleteDeadline = event.getEventDate().plusDays(1).atTime(23, 59, 59);
            LocalDateTime now = LocalDateTime.now();
            
            // Can delete only during event and 1 day after
            boolean canDelete = now.isAfter(event.getEventDate().atStartOfDay()) && 
                              now.isBefore(deleteDeadline);
            
            List<ImageResponse> imageResponses = eventImages.stream()
                    .map(this::convertToImageResponse)
                    .collect(Collectors.toList());
            
            GuestDashboardResponse.GuestEventInfo eventInfo = GuestDashboardResponse.GuestEventInfo.builder()
                    .eventId(event.getId())
                    .eventName(event.getName())
                    .eventCode(event.getEventCode())
                    .eventType(event.getEventType().toString())
                    .eventDate(event.getEventDate().atStartOfDay())
                    .uploadCount(eventImages.size())
                    .canDelete(canDelete)
                    .deleteDeadline(deleteDeadline)
                    .uploadedImages(imageResponses)
                    .build();
            
            eventInfos.add(eventInfo);
        }
        
        return GuestDashboardResponse.builder()
                .guestId(guest.getId())
                .guestName(guest.getName())
                .events(eventInfos)
                .build();
    }
    
    @Transactional
    public void deleteGuestImage(Long guestId, Long imageId) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new RuntimeException("Guest not found"));
        
        Image image = guest.getUploadedImages().stream()
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Image not found or doesn't belong to guest"));
        
        // Check if deletion is allowed (during event and 1 day after)
        Event event = image.getEvent();
        LocalDateTime deleteDeadline = event.getEventDate().plusDays(1).atTime(23, 59, 59);
        LocalDateTime now = LocalDateTime.now();
        
        boolean canDelete = now.isAfter(event.getEventDate().atStartOfDay()) && 
                          now.isBefore(deleteDeadline);
        
        if (!canDelete) {
            throw new RuntimeException("Image deletion is not allowed. Can only delete during event and 1 day after.");
        }
        
        // Delete will be handled by ImageService
        guest.getUploadedImages().remove(image);
        guestRepository.save(guest);
    }
    
    private ImageResponse convertToImageResponse(Image image) {
        return ImageResponse.builder()
                .id(image.getId())
                .fileName(image.getFileName())
                .originalFileName(image.getOriginalFileName())
                .s3Url(image.getS3Url())
                .thumbnailUrl(image.getThumbnailUrl())
                .fileSizeMB(image.getFileSizeMB())
                .contentType(image.getContentType())
                .guestName(image.getGuest().getName())
                .uploadedAt(image.getUploadedAt())
                .build();
    }
}
