package com.example.myeventgallery.service;

import com.example.myeventgallery.dto.AdminDashboardStats;
import com.example.myeventgallery.dto.AdminEventResponse;
import com.example.myeventgallery.dto.AdminLoginRequest;
import com.example.myeventgallery.dto.AuthResponse;
import com.example.myeventgallery.model.*;
import com.example.myeventgallery.repository.*;
import com.example.myeventgallery.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AdminService adminService;

    private Admin admin;
    private Event event;
    private Customer customer;
    private PricingPackage pricingPackage;

    @BeforeEach
    void setUp() {
        admin = new Admin();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setEmail("admin@test.com");
        admin.setPassword("hashedPassword");
        admin.setFullName("Admin User");
        admin.setIsActive(true);

        customer = new Customer();
        customer.setId(1L);
        customer.setName("Test Customer");
        customer.setEmail("customer@test.com");

        pricingPackage = new PricingPackage();
        pricingPackage.setId(1L);
        pricingPackage.setName("Basic");
        pricingPackage.setBasePrice(BigDecimal.valueOf(29.99));

        event = new Event();
        event.setId(1L);
        event.setEventCode("TEST123");
        event.setName("Test Event");
        event.setEventType(EventType.BIRTHDAY);
        event.setEventDate(LocalDate.now());
        event.setCustomer(customer);
        event.setPackagePlan(pricingPackage);
        event.setIsActive(true);
        event.setIsExpired(false);
        event.setTotalUploads(0);
        event.setTotalSizeMB(0.0);
        event.setQrValidUntil(LocalDate.now().plusDays(3).atTime(23, 59, 59)); // Set QR validity
    }

    @Test
    void testAdminLogin_Success() {
        AdminLoginRequest request = new AdminLoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("admin123", "hashedPassword")).thenReturn(true);
        when(jwtUtil.generateCustomerToken(anyString(), any())).thenReturn("test-token");

        AuthResponse response = adminService.adminLogin(request);

        assertNotNull(response);
        assertEquals("test-token", response.getToken());
        verify(adminRepository).findByUsername("admin");
        verify(passwordEncoder).matches("admin123", "hashedPassword");
    }

    @Test
    void testAdminLogin_InvalidCredentials() {
        AdminLoginRequest request = new AdminLoginRequest();
        request.setUsername("admin");
        request.setPassword("wrongPassword");

        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> adminService.adminLogin(request));
    }

    @Test
    void testAdminLogin_InactiveAccount() {
        admin.setIsActive(false);
        AdminLoginRequest request = new AdminLoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("admin123", "hashedPassword")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> adminService.adminLogin(request));
        assertEquals("Admin account is inactive", exception.getMessage());
    }

    @Test
    void testGetDashboardStats() {
        when(customerRepository.count()).thenReturn(10L);
        when(eventRepository.count()).thenReturn(20L);
        when(guestRepository.count()).thenReturn(50L);
        when(imageRepository.count()).thenReturn(200L);
        when(imageRepository.findAll()).thenReturn(Arrays.asList());
        when(eventRepository.findAll()).thenReturn(Arrays.asList(event));

        AdminDashboardStats stats = adminService.getDashboardStats();

        assertNotNull(stats);
        assertEquals(10L, stats.getTotalCustomers());
        assertEquals(20L, stats.getTotalEvents());
        assertEquals(50L, stats.getTotalGuests());
        assertEquals(200L, stats.getTotalImages());
        assertEquals("HEALTHY", stats.getSystemStatus());
    }

    @Test
    void testSearchEvents() {
        List<Event> events = Arrays.asList(event);
        when(eventRepository.findAll()).thenReturn(events);

        List<AdminEventResponse> results = adminService.searchEvents("Test");

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals("Test Event", results.get(0).getName());
    }

    @Test
    void testDeleteEvent() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        adminService.deleteEvent(1L);

        verify(eventRepository).delete(event);
    }

    @Test
    void testDeleteCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        adminService.deleteCustomer(1L);

        verify(customerRepository).delete(customer);
    }
}
