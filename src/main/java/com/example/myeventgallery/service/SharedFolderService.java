package com.example.myeventgallery.service;

import com.example.myeventgallery.dto.CreateSharedFolderRequest;
import com.example.myeventgallery.dto.SharedFolderResponse;
import com.example.myeventgallery.dto.ImageResponse;
import com.example.myeventgallery.model.Customer;
import com.example.myeventgallery.model.Event;
import com.example.myeventgallery.model.Image;
import com.example.myeventgallery.model.SharedFolder;
import com.example.myeventgallery.repository.EventRepository;
import com.example.myeventgallery.repository.ImageRepository;
import com.example.myeventgallery.repository.SharedFolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class SharedFolderService {
    
    @Autowired
    private SharedFolderRepository sharedFolderRepository;
    
    @Autowired
    private ImageRepository imageRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private StorageService storageService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
    
    @Transactional
    public SharedFolderResponse createSharedFolder(CreateSharedFolderRequest request, Long customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        // Verify customer owns the event
        if (!event.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        // Get images
        List<Image> images = imageRepository.findAllById(request.getImageIds());
        
        SharedFolder folder = new SharedFolder();
        folder.setFolderName(request.getFolderName());
        folder.setEvent(event);
        folder.setCustomer(customer);
        folder.setImages(images);
        folder.setIsActive(true);
        folder.setExpiryDate(request.getExpiryDate());
        
        // Hash password if provided
        if (request.getAccessPassword() != null && !request.getAccessPassword().isEmpty()) {
            folder.setAccessPassword(passwordEncoder.encode(request.getAccessPassword()));
        }
        
        folder = sharedFolderRepository.save(folder);
        
        return convertToResponse(folder);
    }
    
    public List<SharedFolderResponse> getCustomerFolders(Long customerId) {
        return sharedFolderRepository.findByCustomerId(customerId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public SharedFolderResponse getSharedFolder(String shareCode, String password) {
        SharedFolder folder = sharedFolderRepository.findByShareCode(shareCode)
                .orElseThrow(() -> new RuntimeException("Shared folder not found"));
        
        if (!folder.getIsActive()) {
            throw new RuntimeException("Shared folder is no longer active");
        }
        
        if (folder.getExpiryDate() != null && folder.getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Shared folder has expired");
        }
        
        // Check password if required
        if (folder.getAccessPassword() != null && !folder.getAccessPassword().isEmpty()) {
            if (password == null || !passwordEncoder.matches(password, folder.getAccessPassword())) {
                throw new RuntimeException("Invalid password");
            }
        }
        
        return convertToResponse(folder);
    }
    
    @Transactional
    public void incrementDownloadCount(Long folderId) {
        SharedFolder folder = sharedFolderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));
        
        folder.setDownloadCount(folder.getDownloadCount() + 1);
        sharedFolderRepository.save(folder);
    }
    
    @Transactional
    public void updateFolderImages(Long folderId, List<Long> imageIds, Long customerId) {
        SharedFolder folder = sharedFolderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));
        
        if (!folder.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        List<Image> images = imageRepository.findAllById(imageIds);
        folder.setImages(images);
        sharedFolderRepository.save(folder);
    }
    
    @Transactional
    public void deleteSharedFolder(Long folderId, Long customerId) {
        SharedFolder folder = sharedFolderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));
        
        if (!folder.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        sharedFolderRepository.delete(folder);
    }
    
    private SharedFolderResponse convertToResponse(SharedFolder folder) {
        List<ImageResponse> imageResponses = folder.getImages().stream()
                .map(this::convertImageToResponse)
                .collect(Collectors.toList());
        
        return SharedFolderResponse.builder()
                .id(folder.getId())
                .folderName(folder.getFolderName())
                .shareCode(folder.getShareCode())
                .shareUrl(baseUrl + "/shared/" + folder.getShareCode())
                .hasPassword(folder.getAccessPassword() != null && !folder.getAccessPassword().isEmpty())
                .imageCount(folder.getImages().size())
                .expiryDate(folder.getExpiryDate())
                .isActive(folder.getIsActive())
                .downloadCount(folder.getDownloadCount())
                .createdAt(folder.getCreatedAt())
                .images(imageResponses)
                .build();
    }
    
    private ImageResponse convertImageToResponse(Image image) {
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
