package com.example.eventphoto.service;

import com.example.eventphoto.dto.ImageIdsRequest;
import com.example.eventphoto.dto.ImageResponse;
import com.example.eventphoto.model.Event;
import com.example.eventphoto.model.Guest;
import com.example.eventphoto.model.Image;
import com.example.eventphoto.repository.EventRepository;
import com.example.eventphoto.repository.GuestRepository;
import com.example.eventphoto.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final ImageRepository imageRepository;
    private final EventRepository eventRepository;
    private final GuestRepository guestRepository;
    private final StorageService storageService;
    private final ContentModerationService contentModerationService;
    private final DuplicateDetectionService duplicateDetectionService;
    private final AppPropertyService appPropertyService;

    @Transactional
    public List<ImageResponse> upload(Long eventId, Long guestId, MultipartFile[] files) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
        Guest guest = guestRepository.findById(guestId).orElseThrow(() -> new RuntimeException("Guest not found"));
        if (!guest.getEvent().getId().equals(eventId)) {
            throw new RuntimeException("Guest does not belong to this event");
        }
        LocalDateTime now = event.getEventDate().atTime(event.getEventStartTime() != null ? event.getEventStartTime() : java.time.LocalTime.MIN);
        if (LocalDateTime.now().isBefore(now)) {
            throw new RuntimeException("Upload is allowed only after event start time");
        }
        int maxPerBatch = appPropertyService.getGuestUploadMaxImagesPerBatch();
        if (files.length > maxPerBatch) {
            throw new RuntimeException("Maximum " + maxPerBatch + " images per upload allowed");
        }

        List<ImageResponse> saved = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            if (!contentModerationService.isAllowed(file)) {
                log.warn("Content moderation rejected image: {}", file.getOriginalFilename());
                continue;
            }
            String hash;
            try {
                hash = duplicateDetectionService.computeHash(file);
            } catch (Exception e) {
                log.warn("Could not compute perceptual hash", e);
                hash = null;
            }
            if (hash != null) {
                Optional<Long> duplicate = duplicateDetectionService.findDuplicate(eventId, guestId, hash);
                if (duplicate.isPresent()) {
                    log.info("Skipping duplicate image for guest {} event {}", guestId, eventId);
                    continue;
                }
            }
            String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "image";
            String ext = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf('.')) : ".jpg";
            String storageKey = event.getStorageFolderPath() + "g" + guestId + "_" + UUID.randomUUID().toString() + ext;
            try {
                storageService.upload(storageKey, file.getInputStream(), file.getContentType());
            } catch (IOException e) {
                throw new RuntimeException("Upload failed: " + e.getMessage());
            }
            double sizeMb = file.getSize() / (1024.0 * 1024.0);
            Image image = Image.builder()
                    .fileName(storageKey)
                    .originalFileName(originalName)
                    .storageKey(storageKey)
                    .storageUrl(storageService.getPublicUrl(storageKey))
                    .fileSizeMb(sizeMb)
                    .contentType(file.getContentType())
                    .perceptualHash(hash)
                    .event(event)
                    .guest(guest)
                    .build();
            image = imageRepository.save(image);
            guest.setUploadCount(guest.getUploadCount() + 1);
            guestRepository.save(guest);
            saved.add(toImageResponse(image));
        }
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Image> findByIds(List<Long> ids) {
        return imageRepository.findByIdIn(ids);
    }

    @Transactional
    public void bulkDelete(List<Long> imageIds, Long customerId) {
        List<Image> images = imageRepository.findByIdIn(imageIds);
        for (Image img : images) {
            if (!img.getEvent().getCustomer().getId().equals(customerId)) {
                throw new RuntimeException("Access denied to delete image");
            }
            try {
                storageService.deleteFile(img.getStorageKey());
            } catch (Exception e) {
                log.warn("Could not delete file from storage: {}", img.getStorageKey());
            }
            imageRepository.delete(img);
        }
    }

    @Transactional
    public void guestDeleteOwnImage(Long imageId, Long guestId) {
        Image image = imageRepository.findById(imageId).orElseThrow(() -> new RuntimeException("Image not found"));
        if (!image.getGuest().getId().equals(guestId)) {
            throw new RuntimeException("You can only delete your own images");
        }
        try {
            storageService.deleteFile(image.getStorageKey());
        } catch (Exception e) {
            log.warn("Could not delete file from storage: {}", image.getStorageKey());
        }
        imageRepository.delete(image);
    }

    @Transactional(readOnly = true)
    public byte[] downloadAsZip(List<Long> imageIds, Long customerId) throws IOException {
        List<Image> images = imageRepository.findByIdIn(imageIds);
        for (Image img : images) {
            if (!img.getEvent().getCustomer().getId().equals(customerId)) {
                throw new RuntimeException("Access denied");
            }
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (Image img : images) {
                byte[] bytes = storageService.getFileBytes(img.getStorageKey());
                String entryName = img.getOriginalFileName() != null ? img.getOriginalFileName() : img.getFileName();
                if (images.size() > 1) {
                    entryName = img.getGuest().getName().replaceAll("[^a-zA-Z0-9.-]", "_") + "_" + entryName;
                }
                zos.putNextEntry(new ZipEntry(entryName));
                zos.write(bytes);
                zos.closeEntry();
            }
        }
        return baos.toByteArray();
    }

    private ImageResponse toImageResponse(Image img) {
        return ImageResponse.builder()
                .id(img.getId())
                .fileName(img.getFileName())
                .originalFileName(img.getOriginalFileName())
                .storageUrl(storageService.getPublicUrl(img.getStorageKey()))
                .fileSizeMb(img.getFileSizeMb())
                .contentType(img.getContentType())
                .eventId(img.getEvent().getId())
                .guestId(img.getGuest().getId())
                .guestName(img.getGuest().getName())
                .uploadedAt(img.getUploadedAt())
                .build();
    }
}
