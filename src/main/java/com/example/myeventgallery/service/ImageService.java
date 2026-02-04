package com.example.myeventgallery.service;

import com.example.myeventgallery.dto.EventImagesGroupedResponse;
import com.example.myeventgallery.dto.ImageResponse;
import com.example.myeventgallery.model.Event;
import com.example.myeventgallery.model.Guest;
import com.example.myeventgallery.model.Image;
import com.example.myeventgallery.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ImageService {
    
    @Autowired
    private ImageRepository imageRepository;
    
    @Autowired
    private StorageService storageService;
    
    @Autowired
    private GuestService guestService;
    
    @Autowired
    private EventService eventService;
    
    @Transactional
    public List<ImageResponse> uploadImages(List<MultipartFile> files, Long guestId) throws IOException {
        Guest guest = guestService.getGuestById(guestId);
        Event event = guest.getEvent();
        
        if (!event.getIsActive()) {
            throw new RuntimeException("Event is not active");
        }
        
        if (event.getIsExpired()) {
            throw new RuntimeException("Event has expired");
        }
        
        // Check package limits
        com.example.myeventgallery.model.PricingPackage packagePlan = event.getPackagePlan();
        Long currentImageCount = imageRepository.countByEventId(event.getId());
        
        if (packagePlan.getMaxImages() != null && 
            currentImageCount + files.size() > packagePlan.getMaxImages()) {
            throw new RuntimeException("Image upload limit exceeded for this event");
        }
        
        List<ImageResponse> responses = new ArrayList<>();
        
        for (MultipartFile file : files) {
            // Validate file
            if (file.isEmpty()) {
                continue;
            }
            
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Only image files are allowed");
            }
            
            // Upload to storage (local or S3 based on profile)
            String storageKey = storageService.uploadFile(file, event.getS3FolderPath());
            String fileUrl = storageService.getFileUrl(storageKey);
            
            // Calculate file size in MB
            double fileSizeMB = file.getSize() / (1024.0 * 1024.0);
            
            // Create image record
            Image image = new Image();
            image.setFileName(storageKey.substring(storageKey.lastIndexOf("/") + 1));
            image.setOriginalFileName(file.getOriginalFilename());
            image.setS3Key(storageKey);
            image.setS3Url(fileUrl);
            image.setFileSizeMB(fileSizeMB);
            image.setContentType(contentType);
            image.setEvent(event);
            image.setGuest(guest);
            
            image = imageRepository.save(image);
            
            // Update guest upload count
            guestService.incrementUploadCount(guest);
            
            // Update event stats
            eventService.updateEventStats(event, fileSizeMB);
            
            responses.add(convertToResponse(image));
        }
        
        return responses;
    }
    
    public List<ImageResponse> getEventImages(Long eventId) {
        return imageRepository.findByEventId(eventId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public Page<ImageResponse> getEventImagesPaginated(Long eventId, Pageable pageable) {
        return imageRepository.findByEventId(eventId, pageable)
                .map(this::convertToResponse);
    }
    
    public List<ImageResponse> getEventImagesByCode(String eventCode) {
        return imageRepository.findByEventEventCode(eventCode).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteImage(Long imageId, Long customerId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        
        if (!image.getEvent().getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        // Delete from storage
        storageService.deleteFile(image.getS3Key());
        
        // Delete from database
        imageRepository.delete(image);
    }
    
    /**
     * Get images grouped by guest for customer dashboard
     */
    public EventImagesGroupedResponse getEventImagesGroupedByGuest(Long eventId, Long customerId) {
        List<Image> images = imageRepository.findByEventId(eventId);
        
        if (images.isEmpty()) {
            throw new RuntimeException("No images found for this event");
        }
        
        // Verify customer owns the event
        Event event = images.get(0).getEvent();
        if (!event.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        // Group images by guest
        Map<Guest, List<Image>> imagesByGuest = images.stream()
                .collect(Collectors.groupingBy(Image::getGuest));
        
        // Convert to response format
        Map<String, EventImagesGroupedResponse.GuestImagesGroup> guestGroups = new LinkedHashMap<>();
        
        for (Map.Entry<Guest, List<Image>> entry : imagesByGuest.entrySet()) {
            Guest guest = entry.getKey();
            List<Image> guestImages = entry.getValue();
            
            List<ImageResponse> imageResponses = guestImages.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            EventImagesGroupedResponse.GuestImagesGroup group = EventImagesGroupedResponse.GuestImagesGroup.builder()
                    .guestId(guest.getId())
                    .guestName(guest.getName())
                    .guestEmail(guest.getEmail())
                    .imageCount(guestImages.size())
                    .images(imageResponses)
                    .build();
            
            guestGroups.put(guest.getName(), group);
        }
        
        return EventImagesGroupedResponse.builder()
                .eventId(event.getId())
                .eventName(event.getName())
                .totalImages(images.size())
                .totalGuests(guestGroups.size())
                .guestGroups(guestGroups)
                .build();
    }
    
    /**
     * Download selected images as ZIP
     */
    public byte[] downloadImagesAsZip(List<Long> imageIds, Long customerId) throws IOException {
        List<Image> images = imageRepository.findAllById(imageIds);
        
        if (images.isEmpty()) {
            throw new RuntimeException("No images found");
        }
        
        // Verify customer owns all images
        boolean allOwned = images.stream()
                .allMatch(img -> img.getEvent().getCustomer().getId().equals(customerId));
        
        if (!allOwned) {
            throw new RuntimeException("Unauthorized access to some images");
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (Image image : images) {
                try {
                    // Get file bytes from storage
                    byte[] fileBytes;
                    if (storageService instanceof LocalStorageService) {
                        fileBytes = ((LocalStorageService) storageService).getFile(image.getS3Key());
                    } else {
                        // For S3, we'd need to download the file
                        // This would require adding a method to StorageService interface
                        throw new RuntimeException("Bulk download from S3 not yet implemented");
                    }
                    
                    // Create ZIP entry with guest folder structure
                    String zipEntryName = image.getGuest().getName() + "/" + image.getOriginalFileName();
                    ZipEntry zipEntry = new ZipEntry(zipEntryName);
                    zos.putNextEntry(zipEntry);
                    zos.write(fileBytes);
                    zos.closeEntry();
                    
                } catch (IOException e) {
                    // Log error but continue with other files
                    System.err.println("Error adding file to ZIP: " + image.getFileName());
                }
            }
        }
        
        return baos.toByteArray();
    }
    
    private ImageResponse convertToResponse(Image image) {
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
