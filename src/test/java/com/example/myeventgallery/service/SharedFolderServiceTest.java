package com.example.myeventgallery.service;

import com.example.myeventgallery.dto.CreateSharedFolderRequest;
import com.example.myeventgallery.dto.SharedFolderResponse;
import com.example.myeventgallery.model.*;
import com.example.myeventgallery.repository.*;
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
class SharedFolderServiceTest {

    @Mock
    private SharedFolderRepository sharedFolderRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CustomerService customerService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SharedFolderService sharedFolderService;

    private Customer customer;
    private Event event;
    private Image image;
    private SharedFolder folder;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Test Customer");

        event = new Event();
        event.setId(1L);
        event.setEventCode("TEST123");
        event.setName("Test Event");
        event.setCustomer(customer);
        event.setQrValidUntil(LocalDateTime.now().plusDays(1)); // Set valid until

        Guest guest = new Guest();
        guest.setId(1L);
        guest.setName("Test Guest");

        image = new Image();
        image.setId(1L);
        image.setFileName("test.jpg");
        image.setOriginalFileName("test.jpg");
        image.setS3Url("test-url");
        image.setFileSizeMB(1.5);
        image.setContentType("image/jpeg");
        image.setGuest(guest); // Set guest reference
        image.setUploadedAt(LocalDateTime.now());

        folder = new SharedFolder();
        folder.setId(1L);
        folder.setFolderName("Wedding Favorites");
        folder.setShareCode("ABC123DEF456");
        folder.setEvent(event);
        folder.setCustomer(customer);
        folder.setImages(new ArrayList<>(Arrays.asList(image)));
        folder.setIsActive(true);
        folder.setDownloadCount(0);
        folder.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateSharedFolder_Success() {
        CreateSharedFolderRequest request = new CreateSharedFolderRequest();
        request.setFolderName("Wedding Favorites");
        request.setEventId(1L);
        request.setImageIds(Arrays.asList(1L));
        request.setAccessPassword("password123");

        when(customerService.getCustomerById(1L)).thenReturn(customer);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(imageRepository.findAllById(anyList())).thenReturn(Arrays.asList(image));
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(sharedFolderRepository.save(any(SharedFolder.class))).thenReturn(folder);

        SharedFolderResponse response = sharedFolderService.createSharedFolder(request, 1L);

        assertNotNull(response);
        assertEquals("Wedding Favorites", response.getFolderName());
        assertEquals("ABC123DEF456", response.getShareCode());
        verify(sharedFolderRepository).save(any(SharedFolder.class));
    }

    @Test
    void testCreateSharedFolder_UnauthorizedAccess() {
        CreateSharedFolderRequest request = new CreateSharedFolderRequest();
        request.setEventId(1L);

        Customer differentCustomer = new Customer();
        differentCustomer.setId(2L);

        when(customerService.getCustomerById(2L)).thenReturn(differentCustomer);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(RuntimeException.class,
            () -> sharedFolderService.createSharedFolder(request, 2L));
    }

    @Test
    void testGetSharedFolder_Success() {
        when(sharedFolderRepository.findByShareCode("ABC123DEF456")).thenReturn(Optional.of(folder));

        SharedFolderResponse response = sharedFolderService.getSharedFolder("ABC123DEF456", null);

        assertNotNull(response);
        assertEquals("Wedding Favorites", response.getFolderName());
    }

    @Test
    void testGetSharedFolder_WithPassword_Success() {
        folder.setAccessPassword("hashedPassword");

        when(sharedFolderRepository.findByShareCode("ABC123DEF456")).thenReturn(Optional.of(folder));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);

        SharedFolderResponse response = sharedFolderService.getSharedFolder("ABC123DEF456", "password123");

        assertNotNull(response);
    }

    @Test
    void testGetSharedFolder_WithPassword_InvalidPassword() {
        folder.setAccessPassword("hashedPassword");

        when(sharedFolderRepository.findByShareCode("ABC123DEF456")).thenReturn(Optional.of(folder));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        assertThrows(RuntimeException.class,
            () -> sharedFolderService.getSharedFolder("ABC123DEF456", "wrongPassword"));
    }

    @Test
    void testGetSharedFolder_Expired() {
        folder.setExpiryDate(LocalDateTime.now().minusDays(1));

        when(sharedFolderRepository.findByShareCode("ABC123DEF456")).thenReturn(Optional.of(folder));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> sharedFolderService.getSharedFolder("ABC123DEF456", null));
        
        assertEquals("Shared folder has expired", exception.getMessage());
    }

    @Test
    void testGetSharedFolder_Inactive() {
        folder.setIsActive(false);

        when(sharedFolderRepository.findByShareCode("ABC123DEF456")).thenReturn(Optional.of(folder));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> sharedFolderService.getSharedFolder("ABC123DEF456", null));
        
        assertEquals("Shared folder is no longer active", exception.getMessage());
    }

    @Test
    void testIncrementDownloadCount() {
        folder.setDownloadCount(5);

        when(sharedFolderRepository.findById(1L)).thenReturn(Optional.of(folder));
        when(sharedFolderRepository.save(any(SharedFolder.class))).thenReturn(folder);

        sharedFolderService.incrementDownloadCount(1L);

        verify(sharedFolderRepository).save(argThat(f -> f.getDownloadCount() == 6));
    }

    @Test
    void testDeleteSharedFolder() {
        when(sharedFolderRepository.findById(1L)).thenReturn(Optional.of(folder));

        sharedFolderService.deleteSharedFolder(1L, 1L);

        verify(sharedFolderRepository).delete(folder);
    }

    @Test
    void testDeleteSharedFolder_UnauthorizedAccess() {
        when(sharedFolderRepository.findById(1L)).thenReturn(Optional.of(folder));

        assertThrows(RuntimeException.class,
            () -> sharedFolderService.deleteSharedFolder(1L, 2L));
    }
}
