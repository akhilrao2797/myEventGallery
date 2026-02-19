package com.example.eventphoto.service;

import com.example.eventphoto.dto.SharedLinkCreateRequest;
import com.example.eventphoto.dto.SharedLinkResponse;
import com.example.eventphoto.model.*;
import com.example.eventphoto.repository.ImageRepository;
import com.example.eventphoto.repository.SharedLinkRepository;
import com.example.eventphoto.repository.EventRepository;
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
public class ShareLinkService {

    private final SharedLinkRepository sharedLinkRepository;
    private final ImageRepository imageRepository;
    private final EventRepository eventRepository;
    private final StorageService storageService;
    private final PasswordEncoder passwordEncoder;

    private static final int SHARE_CODE_LENGTH = 12;

    @Transactional
    public SharedLinkResponse create(SharedLinkCreateRequest request, Long customerId, Customer customer) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));
        if (!event.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Event not found");
        }
        List<Image> images = imageRepository.findByIdIn(request.getImageIds());
        for (Image img : images) {
            if (!img.getEvent().getId().equals(request.getEventId())) {
                throw new RuntimeException("Image does not belong to event");
            }
            if (!img.getEvent().getCustomer().getId().equals(customerId)) {
                throw new RuntimeException("Access denied");
            }
        }
        String shareCode = UUID.randomUUID().toString().replace("-", "").substring(0, SHARE_CODE_LENGTH);
        while (sharedLinkRepository.findByShareCode(shareCode).isPresent()) {
            shareCode = UUID.randomUUID().toString().replace("-", "").substring(0, SHARE_CODE_LENGTH);
        }
        String passwordHash = request.getPassword() != null && !request.getPassword().isBlank()
                ? passwordEncoder.encode(request.getPassword())
                : null;
        SharedLink link = SharedLink.builder()
                .shareCode(shareCode)
                .folderName(request.getFolderName())
                .accessPasswordHash(passwordHash)
                .expiresAt(request.getExpiresAt())
                .event(event)
                .customer(customer)
                .images(images)
                .isActive(true)
                .build();
        link = sharedLinkRepository.save(link);
        String fullUrl = "/shared/" + shareCode;
        return SharedLinkResponse.builder()
                .id(link.getId())
                .shareCode(shareCode)
                .shareUrl(fullUrl)
                .url(fullUrl)
                .folderName(link.getFolderName())
                .hasPassword(passwordHash != null)
                .hasExpiry(link.getExpiresAt() != null)
                .expiresAt(link.getExpiresAt())
                .imageCount(images.size())
                .build();
    }

    @Transactional(readOnly = true)
    public SharedLinkResponse getByShareCodePublic(String shareCode, String password) {
        SharedLink link = sharedLinkRepository.findByShareCode(shareCode)
                .orElseThrow(() -> new RuntimeException("Share link not found"));
        if (!link.getIsActive()) {
            throw new RuntimeException("Link is no longer active");
        }
        if (link.isExpired()) {
            throw new RuntimeException("Link has expired");
        }
        if (link.getAccessPasswordHash() != null) {
            if (password == null || password.isBlank() || !passwordEncoder.matches(password, link.getAccessPasswordHash())) {
                throw new RuntimeException("Invalid or missing password");
            }
        }
        return SharedLinkResponse.builder()
                .shareCode(link.getShareCode())
                .folderName(link.getFolderName())
                .hasExpiry(link.getExpiresAt() != null)
                .expiresAt(link.getExpiresAt())
                .imageCount(link.getImages() != null ? link.getImages().size() : 0)
                .imageUrls(link.getImages() == null ? List.of() : link.getImages().stream()
                        .map(img -> storageService.getPublicUrl(img.getStorageKey()))
                        .collect(Collectors.toList()))
                .build();
    }

    @Transactional(readOnly = true)
    public List<SharedLinkResponse> listByCustomer(Long customerId) {
        List<SharedLink> links = sharedLinkRepository.findByCustomerId(customerId);
        return links.stream().map(link -> SharedLinkResponse.builder()
                .id(link.getId())
                .shareCode(link.getShareCode())
                .shareUrl("/shared/" + link.getShareCode())
                .url("/shared/" + link.getShareCode())
                .folderName(link.getFolderName())
                .hasPassword(link.getAccessPasswordHash() != null)
                .hasExpiry(link.getExpiresAt() != null)
                .expiresAt(link.getExpiresAt())
                .imageCount(link.getImages() != null ? link.getImages().size() : 0)
                .build()).collect(Collectors.toList());
    }
}
