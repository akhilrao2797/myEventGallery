package com.example.myeventgallery.service;

import com.example.myeventgallery.dto.EventCreateRequest;
import com.example.myeventgallery.dto.EventResponse;
import com.example.myeventgallery.model.Customer;
import com.example.myeventgallery.model.Event;
import com.example.myeventgallery.model.PricingPackage;
import com.example.myeventgallery.model.Payment;
import com.example.myeventgallery.repository.EventRepository;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private PackageService packageService;
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private QRCodeService qrCodeService;
    
    @Autowired
    private S3Service s3Service;
    
    @Transactional
    public EventResponse createEvent(EventCreateRequest request, Long customerId) throws WriterException, IOException {
        Customer customer = customerService.getCustomerById(customerId);
        PricingPackage packagePlan = packageService.getPackageByType(request.getPackageType());
        
        Event event = new Event();
        event.setName(request.getName());
        event.setEventType(request.getEventType());
        event.setDescription(request.getDescription());
        event.setEventDate(request.getEventDate());
        event.setVenue(request.getVenue());
        event.setExpectedGuests(request.getExpectedGuests());
        event.setCustomer(customer);
        event.setPackagePlan(packagePlan);
        event.setIsActive(true);
        event.setIsExpired(false);
        
        // Calculate expiry date based on package storage days
        LocalDate expiryDate = request.getEventDate().plusDays(packagePlan.getStorageDays());
        event.setExpiryDate(expiryDate);
        
        event = eventRepository.save(event);
        
        // Generate QR Code and upload to S3
        byte[] qrCodeBytes = qrCodeService.generateQRCode(event.getEventCode());
        String qrCodeKey = event.getS3FolderPath() + "qr-code.png";
        
        // For now, we'll store the QR code URL as a presigned URL
        // In production, you might want to upload it to S3
        event.setQrCodeUrl("/api/events/qr/" + event.getEventCode());
        
        event = eventRepository.save(event);
        
        // Create payment record
        Payment payment = paymentService.createPayment(event, packagePlan);
        
        return convertToResponse(event);
    }
    
    public EventResponse getEventById(Long eventId, Long customerId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        if (!event.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        return convertToResponse(event);
    }
    
    public Event getEventByCode(String eventCode) {
        return eventRepository.findByEventCode(eventCode)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }
    
    public List<EventResponse> getCustomerEvents(Long customerId) {
        return eventRepository.findByCustomerId(customerId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void updateEventStats(Event event, double fileSizeMB) {
        event.setTotalUploads(event.getTotalUploads() + 1);
        event.setTotalSizeMB(event.getTotalSizeMB() + fileSizeMB);
        eventRepository.save(event);
    }
    
    public byte[] getQRCode(String eventCode) throws WriterException, IOException {
        Event event = getEventByCode(eventCode);
        return qrCodeService.generateQRCode(event.getEventCode());
    }
    
    private EventResponse convertToResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .eventCode(event.getEventCode())
                .name(event.getName())
                .eventType(event.getEventType())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .venue(event.getVenue())
                .expectedGuests(event.getExpectedGuests())
                .qrCodeUrl(event.getQrCodeUrl())
                .packageName(event.getPackagePlan().getName())
                .isActive(event.getIsActive())
                .isExpired(event.getIsExpired())
                .expiryDate(event.getExpiryDate())
                .totalUploads(event.getTotalUploads())
                .totalSizeMB(event.getTotalSizeMB())
                .guestCount(event.getGuests().size())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
