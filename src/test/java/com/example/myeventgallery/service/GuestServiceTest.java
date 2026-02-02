package com.example.myeventgallery.service;

import com.example.myeventgallery.dto.GuestRegistrationRequest;
import com.example.myeventgallery.dto.GuestResponse;
import com.example.myeventgallery.model.Event;
import com.example.myeventgallery.model.Guest;
import com.example.myeventgallery.repository.GuestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuestServiceTest {

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private EventService eventService;

    @InjectMocks
    private GuestService guestService;

    private Event event;
    private Guest guest;
    private GuestRegistrationRequest registrationRequest;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(1L);
        event.setEventCode("test-event-code");
        event.setIsActive(true);
        event.setIsExpired(false);

        guest = new Guest();
        guest.setId(1L);
        guest.setName("Jane Doe");
        guest.setEmail("jane@example.com");
        guest.setPhoneNumber("+1234567890");
        guest.setEvent(event);
        guest.setUploadCount(0);

        registrationRequest = new GuestRegistrationRequest();
        registrationRequest.setName("Jane Doe");
        registrationRequest.setEmail("jane@example.com");
        registrationRequest.setPhoneNumber("+1234567890");
        registrationRequest.setEventCode("test-event-code");
    }

    @Test
    void testRegisterGuestSuccess() {
        when(eventService.getEventByCode("test-event-code")).thenReturn(event);
        when(guestRepository.save(any(Guest.class))).thenReturn(guest);

        GuestResponse response = guestService.registerGuest(registrationRequest);

        assertNotNull(response);
        assertEquals("Jane Doe", response.getName());
        assertEquals("jane@example.com", response.getEmail());
        assertEquals(0, response.getUploadCount());

        verify(eventService).getEventByCode("test-event-code");
        verify(guestRepository).save(any(Guest.class));
    }

    @Test
    void testRegisterGuestEventNotActive() {
        event.setIsActive(false);
        when(eventService.getEventByCode("test-event-code")).thenReturn(event);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            guestService.registerGuest(registrationRequest);
        });

        assertEquals("Event is not active", exception.getMessage());
        verify(guestRepository, never()).save(any(Guest.class));
    }

    @Test
    void testRegisterGuestEventExpired() {
        event.setIsExpired(true);
        when(eventService.getEventByCode("test-event-code")).thenReturn(event);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            guestService.registerGuest(registrationRequest);
        });

        assertEquals("Event has expired", exception.getMessage());
        verify(guestRepository, never()).save(any(Guest.class));
    }

    @Test
    void testGetGuestById() {
        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));

        Guest result = guestService.getGuestById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Jane Doe", result.getName());
    }

    @Test
    void testGetGuestByIdNotFound() {
        when(guestRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            guestService.getGuestById(1L);
        });

        assertEquals("Guest not found", exception.getMessage());
    }

    @Test
    void testGetEventGuests() {
        List<Guest> guests = Arrays.asList(guest);
        when(guestRepository.findByEventId(1L)).thenReturn(guests);

        List<GuestResponse> responses = guestService.getEventGuests(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Jane Doe", responses.get(0).getName());
    }

    @Test
    void testIncrementUploadCount() {
        when(guestRepository.save(any(Guest.class))).thenReturn(guest);

        guestService.incrementUploadCount(guest);

        assertEquals(1, guest.getUploadCount());
        verify(guestRepository).save(guest);
    }
}
