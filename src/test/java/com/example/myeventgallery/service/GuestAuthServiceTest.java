package com.example.myeventgallery.service;

import com.example.myeventgallery.dto.GuestDashboardResponse;
import com.example.myeventgallery.dto.GuestLoginRequest;
import com.example.myeventgallery.model.*;
import com.example.myeventgallery.repository.GuestRepository;
import com.example.myeventgallery.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuestAuthServiceTest {

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private GuestAuthService guestAuthService;

    private Guest guest;
    private Event event;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(1L);
        event.setEventCode("TEST123");
        event.setName("Test Event");
        event.setEventDate(LocalDate.now());
        event.setQrValidUntil(LocalDate.now().plusDays(3).atTime(23, 59, 59)); // Set QR validity

        guest = new Guest();
        guest.setId(1L);
        guest.setName("Test Guest");
        guest.setEmail("guest@test.com");
        guest.setPassword("hashedPassword");
        guest.setEvent(event);
        guest.setUploadedImages(new ArrayList<>());
    }

    @Test
    void testGuestLogin_Success() {
        GuestLoginRequest request = new GuestLoginRequest();
        request.setEmail("guest@test.com");
        request.setPassword("password123");
        request.setEventCode("TEST123");

        when(guestRepository.findByEmailAndEventEventCode("guest@test.com", "TEST123"))
                .thenReturn(Optional.of(guest));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtUtil.generateGuestToken(anyString(), any())).thenReturn("guest-token");

        var response = guestAuthService.guestLogin(request);

        assertNotNull(response);
        assertEquals("guest-token", response.getToken());
        assertEquals("Test Guest", response.getName());
        verify(jwtUtil).generateGuestToken("guest@test.com", 1L);
    }

    @Test
    void testGuestLogin_InvalidPassword() {
        GuestLoginRequest request = new GuestLoginRequest();
        request.setEmail("guest@test.com");
        request.setPassword("wrongPassword");
        request.setEventCode("TEST123");

        when(guestRepository.findByEmailAndEventEventCode("guest@test.com", "TEST123"))
                .thenReturn(Optional.of(guest));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> guestAuthService.guestLogin(request));
    }

    @Test
    void testGuestLogin_GuestNotFound() {
        GuestLoginRequest request = new GuestLoginRequest();
        request.setEmail("notfound@test.com");
        request.setPassword("password123");
        request.setEventCode("TEST123");

        when(guestRepository.findByEmailAndEventEventCode("notfound@test.com", "TEST123"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> guestAuthService.guestLogin(request));
        assertEquals("Guest not found for this event", exception.getMessage());
    }

    @Test
    void testGetGuestDashboard() {
        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));

        GuestDashboardResponse dashboard = guestAuthService.getGuestDashboard(1L);

        assertNotNull(dashboard);
        assertEquals(1L, dashboard.getGuestId());
        assertEquals("Test Guest", dashboard.getGuestName());
        verify(guestRepository).findById(1L);
    }

    @Test
    void testDeleteGuestImage_DuringAllowedWindow() {
        Image image = new Image();
        image.setId(1L);
        image.setGuest(guest);
        
        // Set event date to today (within allowed window)
        event.setEventDate(LocalDate.now());
        image.setEvent(event);
        
        List<Image> images = new ArrayList<>();
        images.add(image);
        guest.setUploadedImages(images);

        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));
        when(guestRepository.save(any(Guest.class))).thenReturn(guest);

        // Should not throw exception during allowed window
        guestAuthService.deleteGuestImage(1L, 1L);

        verify(guestRepository).save(guest);
    }

    @Test
    void testDeleteGuestImage_OutsideAllowedWindow() {
        Image image = new Image();
        image.setId(1L);
        image.setGuest(guest);
        
        // Set event date to 5 days ago (outside allowed window)
        event.setEventDate(LocalDate.now().minusDays(5));
        image.setEvent(event);
        
        List<Image> images = new ArrayList<>();
        images.add(image);
        guest.setUploadedImages(images);

        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> guestAuthService.deleteGuestImage(1L, 1L));
        
        assertTrue(exception.getMessage().contains("Image deletion is not allowed"));
    }

    @Test
    void testDeleteGuestImage_ImageNotFound() {
        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));

        assertThrows(RuntimeException.class,
            () -> guestAuthService.deleteGuestImage(1L, 999L));
    }
}
