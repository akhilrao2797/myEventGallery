package com.example.eventphoto.service;

import com.example.eventphoto.dto.*;
import com.example.eventphoto.model.Event;
import com.example.eventphoto.model.Guest;
import com.example.eventphoto.model.Image;
import com.example.eventphoto.repository.EventRepository;
import com.example.eventphoto.repository.GuestRepository;
import com.example.eventphoto.repository.ImageRepository;
import com.example.eventphoto.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;
    private final EventRepository eventRepository;
    private final ImageRepository imageRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AppPropertyService appPropertyService;
    private final StorageService storageService;

    @Transactional
    public GuestResponse register(GuestRegisterRequest request) {
        Event event = eventRepository.findByEventCode(request.getEventCode().trim())
                .orElseThrow(() -> new RuntimeException("Invalid event code"));
        if (!event.getIsActive()) {
            throw new RuntimeException("Event is not active");
        }
        Guest existing = request.getEmail() != null && !request.getEmail().isBlank()
                ? guestRepository.findByEmailAndEventId(request.getEmail(), event.getId()).orElse(null)
                : null;
        if (existing != null) {
            throw new RuntimeException("Already registered for this event. Please log in.");
        }
        String hashedPassword = request.getPassword() != null && !request.getPassword().isBlank()
                ? passwordEncoder.encode(request.getPassword())
                : null;
        Guest guest = Guest.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(hashedPassword)
                .event(event)
                .uploadCount(0)
                .build();
        guest = guestRepository.save(guest);
        String token = jwtUtil.generateGuestToken(guest.getEmail() != null ? guest.getEmail() : guest.getId().toString(), guest.getId());
        return toGuestResponse(guest, token);
    }

    public GuestResponse login(GuestLoginRequest request) {
        Event event = eventRepository.findByEventCode(request.getEventCode().trim())
                .orElseThrow(() -> new RuntimeException("Invalid event code"));
        Guest guest = guestRepository.findByEmailAndEventId(request.getEmail(), event.getId())
                .orElse(null);
        if (guest == null) {
            throw new RuntimeException("Not registered for this event");
        }
        if (guest.getPassword() != null && request.getPassword() != null) {
            if (!passwordEncoder.matches(request.getPassword(), guest.getPassword())) {
                throw new RuntimeException("Invalid password");
            }
        }
        String token = jwtUtil.generateGuestToken(guest.getEmail() != null ? guest.getEmail() : guest.getId().toString(), guest.getId());
        return toGuestResponse(guest, token);
    }

    public EventPublicInfoDto getEventPublicInfo(String eventCode) {
        Event event = eventRepository.findByEventCode(eventCode)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        return EventPublicInfoDto.builder()
                .eventCode(event.getEventCode())
                .name(event.getName())
                .eventType(event.getEventType())
                .eventDate(event.getEventDate())
                .eventStartTime(event.getEventStartTime())
                .eventEndTime(event.getEventEndTime())
                .venue(event.getVenue())
                .build();
    }

    public boolean canGuestModify(Long guestId) {
        Guest guest = guestRepository.findById(guestId).orElseThrow(() -> new RuntimeException("Guest not found"));
        int daysAllowed = appPropertyService.getGuestModifyDaysAfterEvent();
        LocalDateTime cutoff = guest.getEvent().getEventDate().atStartOfDay().plusDays(daysAllowed);
        return LocalDateTime.now().isBefore(cutoff);
    }

    @Transactional(readOnly = true)
    public List<GuestEventWithImagesDto> getDashboard(Long guestId) {
        Guest guest = guestRepository.findById(guestId).orElseThrow(() -> new RuntimeException("Guest not found"));
        List<Event> events = guestRepository.findEventsByGuestId(guestId);
        int daysAllowed = appPropertyService.getGuestModifyDaysAfterEvent();
        return events.stream().map(evt -> {
            List<Image> images = imageRepository.findByEventIdAndGuestId(evt.getId(), guestId);
            boolean canModify = canGuestModify(guestId);
            java.time.LocalDateTime cutoff = evt.getEventDate().atStartOfDay().plusDays(daysAllowed);
            String modifyDeadlineMessage = canModify
                    ? "You can modify until " + cutoff.toLocalDate()
                    : "Modification window has ended";
            EventPublicInfoDto eventInfo = EventPublicInfoDto.builder()
                    .eventCode(evt.getEventCode())
                    .name(evt.getName())
                    .eventType(evt.getEventType())
                    .eventDate(evt.getEventDate())
                    .eventStartTime(evt.getEventStartTime())
                    .eventEndTime(evt.getEventEndTime())
                    .venue(evt.getVenue())
                    .build();
            return GuestEventWithImagesDto.builder()
                    .eventId(evt.getId())
                    .eventName(evt.getName())
                    .eventInfo(eventInfo)
                    .canModify(canModify)
                    .modifyDeadlineMessage(modifyDeadlineMessage)
                    .images(images.stream().map(img -> com.example.eventphoto.dto.ImageResponse.builder()
                            .id(img.getId())
                            .fileName(img.getFileName())
                            .originalFileName(img.getOriginalFileName())
                            .storageUrl(storageService.getPublicUrl(img.getStorageKey()))
                            .eventId(evt.getId())
                            .guestId(guestId)
                            .uploadedAt(img.getUploadedAt())
                            .build()).collect(Collectors.toList()))
                    .build();
        }).collect(Collectors.toList());
    }

    public Guest findById(Long id) {
        return guestRepository.findById(id).orElseThrow(() -> new RuntimeException("Guest not found"));
    }

    private GuestResponse toGuestResponse(Guest g, String token) {
        return GuestResponse.builder()
                .token(token)
                .id(g.getId())
                .name(g.getName())
                .email(g.getEmail())
                .eventId(g.getEvent().getId())
                .eventCode(g.getEvent().getEventCode())
                .eventName(g.getEvent().getName())
                .build();
    }
}
