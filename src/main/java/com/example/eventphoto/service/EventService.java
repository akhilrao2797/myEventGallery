package com.example.eventphoto.service;

import com.example.eventphoto.dto.EventCreateRequest;
import com.example.eventphoto.dto.EventResponse;
import com.example.eventphoto.dto.EventImagesGroupedResponse;
import com.example.eventphoto.dto.GuestFolderDto;
import com.example.eventphoto.dto.ImageResponse;
import com.example.eventphoto.model.*;
import com.example.eventphoto.repository.EventRepository;
import com.example.eventphoto.repository.ImageRepository;
import com.example.eventphoto.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ImageRepository imageRepository;
    private final PaymentRepository paymentRepository;
    private final QRCodeService qrCodeService;
    private final RazorpayService razorpayService;
    private final StorageService storageService;

    @Value("${app.frontend-base-url:http://localhost:3000}")
    private String frontendBaseUrl;

    @Value("${app.api-base-url:http://localhost:8080}")
    private String apiBaseUrl;

    private static final int QR_VALID_DAYS_AFTER_EVENT = 3;
    private static final BigDecimal DEFAULT_EVENT_AMOUNT_INR = new BigDecimal("29900"); // 299 INR in paise

    @Transactional
    public EventResponse createEvent(EventCreateRequest request, Long customerId, Customer customer) {
        String eventCode = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        if (eventRepository.existsByEventCode(eventCode)) {
            eventCode = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        }

        LocalDateTime qrValidFrom = request.getEventStartTime() != null
                ? request.getEventDate().atTime(request.getEventStartTime())
                : request.getEventDate().atStartOfDay();
        LocalDateTime qrValidUntil = request.getEventDate().plusDays(QR_VALID_DAYS_AFTER_EVENT).atTime(LocalTime.MAX);

        String storageFolderPath = "events/" + eventCode + "/";

        Event event = Event.builder()
                .eventCode(eventCode)
                .name(request.getName())
                .eventType(request.getEventType())
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .eventStartTime(request.getEventStartTime())
                .eventEndTime(request.getEventEndTime())
                .qrValidFrom(qrValidFrom)
                .qrValidUntil(qrValidUntil)
                .venue(request.getVenue())
                .expectedGuests(request.getExpectedGuests())
                .storageFolderPath(storageFolderPath)
                .customer(customer)
                .isActive(true)
                .build();

        event.setQrCodeUrl(apiBaseUrl + "/api/events/qr/" + eventCode);
        event = eventRepository.save(event);

        RazorpayService.OrderCreateResult orderResult = razorpayService.createOrder(DEFAULT_EVENT_AMOUNT_INR, "evt_" + event.getId());
        Payment payment = Payment.builder()
                .event(event)
                .razorpayOrderId(orderResult.getOrderId())
                .amount(DEFAULT_EVENT_AMOUNT_INR.divide(new BigDecimal("100")))
                .currency("INR")
                .status(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);

        return toEventResponse(event, 0, 0);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByCustomerId(Long customerId) {
        List<Event> events = eventRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
        return events.stream().map(e -> toEventResponse(e,
                e.getGuests() != null ? e.getGuests().size() : 0,
                e.getImages() != null ? e.getImages().size() : 0)).collect(Collectors.toList());
    }

    public Event getById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
    }

    public Event getByEventCode(String eventCode) {
        return eventRepository.findByEventCode(eventCode).orElseThrow(() -> new RuntimeException("Event not found"));
    }

    @Transactional(readOnly = true)
    public EventResponse getEventResponse(Long eventId, Long customerId) {
        ensureCustomerOwnsEvent(eventId, customerId);
        Event event = getById(eventId);
        int guestCount = event.getGuests() != null ? event.getGuests().size() : 0;
        int totalImages = event.getImages() != null ? event.getImages().size() : 0;
        return toEventResponse(event, guestCount, totalImages);
    }

    public void ensureCustomerOwnsEvent(Long eventId, Long customerId) {
        Event event = getById(eventId);
        if (!event.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Access denied");
        }
    }

    @Transactional(readOnly = true)
    public EventImagesGroupedResponse getImagesGroupedByGuest(Long eventId) {
        Event event = getById(eventId);
        List<Image> allImages = imageRepository.findByEventId(eventId);
        List<GuestFolderDto> folders = allImages.stream()
                .collect(Collectors.groupingBy(Image::getGuest))
                .entrySet().stream()
                .map(entry -> {
                    Guest g = entry.getKey();
                    List<ImageResponse> images = entry.getValue().stream()
                            .map(img -> ImageResponse.builder()
                                    .id(img.getId())
                                    .fileName(img.getFileName())
                                    .originalFileName(img.getOriginalFileName())
                                    .storageUrl(storageService.getPublicUrl(img.getStorageKey()))
                                    .fileSizeMb(img.getFileSizeMb())
                                    .contentType(img.getContentType())
                                    .eventId(eventId)
                                    .guestId(g.getId())
                                    .guestName(g.getName())
                                    .uploadedAt(img.getUploadedAt())
                                    .build())
                            .collect(Collectors.toList());
                    return GuestFolderDto.builder()
                            .guestId(g.getId())
                            .guestName(g.getName())
                            .guestEmail(g.getEmail())
                            .imageCount(images.size())
                            .images(images)
                            .build();
                })
                .collect(Collectors.toList());

        return EventImagesGroupedResponse.builder()
                .eventId(eventId)
                .eventName(event.getName())
                .totalImages(allImages.size())
                .guestFolders(folders)
                .build();
    }

    public byte[] getQRCodeImage(String eventCode) {
        try {
            return qrCodeService.generateQRCode(eventCode);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    private EventResponse toEventResponse(Event e, int guestCount, int totalImages) {
        return EventResponse.builder()
                .id(e.getId())
                .eventCode(e.getEventCode())
                .name(e.getName())
                .eventType(e.getEventType())
                .description(e.getDescription())
                .eventDate(e.getEventDate())
                .eventStartTime(e.getEventStartTime())
                .eventEndTime(e.getEventEndTime())
                .venue(e.getVenue())
                .expectedGuests(e.getExpectedGuests())
                .qrCodeUrl(e.getQrCodeUrl())
                .customerId(e.getCustomer().getId())
                .isActive(e.getIsActive())
                .createdAt(e.getCreatedAt())
                .guestCount(guestCount)
                .totalImages(totalImages)
                .build();
    }
}
