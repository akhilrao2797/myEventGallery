package com.example.myeventgallery.service;

import com.example.myeventgallery.dto.EventCreateRequest;
import com.example.myeventgallery.dto.EventResponse;
import com.example.myeventgallery.model.*;
import com.example.myeventgallery.repository.EventRepository;
import com.google.zxing.WriterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CustomerService customerService;

    @Mock
    private PackageService packageService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private QRCodeService qrCodeService;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private EventService eventService;

    private Customer customer;
    private PricingPackage pricingPackage;
    private EventCreateRequest createRequest;
    private Event event;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");
        customer.setEmail("john@example.com");

        pricingPackage = new PricingPackage();
        pricingPackage.setId(1L);
        pricingPackage.setPackageType(PackageType.BASIC);
        pricingPackage.setName("Basic Package");
        pricingPackage.setStorageDays(30);
        pricingPackage.setBasePrice(new BigDecimal("29.99"));

        createRequest = new EventCreateRequest();
        createRequest.setName("Test Event");
        createRequest.setEventType(EventType.BIRTHDAY);
        createRequest.setEventDate(LocalDate.now().plusDays(30));
        createRequest.setExpectedGuests(50);
        createRequest.setPackageType(PackageType.BASIC);

        event = new Event();
        event.setId(1L);
        event.setEventCode("test-event-code");
        event.setName("Test Event");
        event.setEventType(EventType.BIRTHDAY);
        event.setEventDate(LocalDate.now().plusDays(30));
        event.setCustomer(customer);
        event.setPackagePlan(pricingPackage);
        event.setIsActive(true);
        event.setIsExpired(false);
        event.setGuests(new ArrayList<>());
        event.setTotalUploads(0);
        event.setTotalSizeMB(0.0);
    }

    @Test
    void testCreateEventSuccess() throws WriterException, IOException {
        when(customerService.getCustomerById(1L)).thenReturn(customer);
        when(packageService.getPackageByType(PackageType.BASIC)).thenReturn(pricingPackage);
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(qrCodeService.generateQRCode(anyString())).thenReturn(new byte[]{1, 2, 3});
        when(paymentService.createPayment(any(Event.class), any(PricingPackage.class)))
                .thenReturn(new Payment());

        EventResponse response = eventService.createEvent(createRequest, 1L);

        assertNotNull(response);
        assertEquals("Test Event", response.getName());
        assertEquals(EventType.BIRTHDAY, response.getEventType());
        assertTrue(response.getIsActive());
        assertFalse(response.getIsExpired());

        verify(eventRepository, times(2)).save(any(Event.class));
        verify(qrCodeService).generateQRCode(anyString());
        verify(paymentService).createPayment(any(Event.class), any(PricingPackage.class));
    }

    @Test
    void testGetEventById() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        EventResponse response = eventService.getEventById(1L, 1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Event", response.getName());
    }

    @Test
    void testGetEventByIdNotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eventService.getEventById(1L, 1L);
        });

        assertEquals("Event not found", exception.getMessage());
    }

    @Test
    void testGetEventByIdUnauthorized() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eventService.getEventById(1L, 999L); // Different customer ID
        });

        assertEquals("Unauthorized access", exception.getMessage());
    }

    @Test
    void testGetCustomerEvents() {
        List<Event> events = Arrays.asList(event);
        when(eventRepository.findByCustomerId(1L)).thenReturn(events);

        List<EventResponse> responses = eventService.getCustomerEvents(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Test Event", responses.get(0).getName());
    }

    @Test
    void testUpdateEventStats() {
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        eventService.updateEventStats(event, 2.5);

        assertEquals(1, event.getTotalUploads());
        assertEquals(2.5, event.getTotalSizeMB());
        verify(eventRepository).save(event);
    }

    @Test
    void testGetQRCode() throws WriterException, IOException {
        byte[] qrCodeBytes = new byte[]{1, 2, 3, 4, 5};
        when(eventRepository.findByEventCode("test-code")).thenReturn(Optional.of(event));
        when(qrCodeService.generateQRCode("test-event-code")).thenReturn(qrCodeBytes);

        byte[] result = eventService.getQRCode("test-code");

        assertNotNull(result);
        assertEquals(5, result.length);
        verify(qrCodeService).generateQRCode("test-event-code");
    }
}
