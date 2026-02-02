package com.example.myeventgallery.service;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageService {
    
    @Autowired
    private ImageRepository imageRepository;
    
    @Autowired
    private S3Service s3Service;
    
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
            
            // Upload to S3
            String s3Key = s3Service.uploadFile(file, event.getS3FolderPath());
            String s3Url = s3Service.getPresignedUrl(s3Key);
            
            // Calculate file size in MB
            double fileSizeMB = file.getSize() / (1024.0 * 1024.0);
            
            // Create image record
            Image image = new Image();
            image.setFileName(s3Key.substring(s3Key.lastIndexOf("/") + 1));
            image.setOriginalFileName(file.getOriginalFilename());
            image.setS3Key(s3Key);
            image.setS3Url(s3Url);
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
        
        // Delete from S3
        s3Service.deleteFile(image.getS3Key());
        
        // Delete from database
        imageRepository.delete(image);
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
