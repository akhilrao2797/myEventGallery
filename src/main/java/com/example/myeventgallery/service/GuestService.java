package com.example.myeventgallery.service;

import com.example.myeventgallery.dto.GuestRegistrationRequest;
import com.example.myeventgallery.dto.GuestResponse;
import com.example.myeventgallery.model.Event;
import com.example.myeventgallery.model.Guest;
import com.example.myeventgallery.repository.GuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuestService {
    
    @Autowired
    private GuestRepository guestRepository;
    
    @Autowired
    private EventService eventService;
    
    @Transactional
    public GuestResponse registerGuest(GuestRegistrationRequest request) {
        Event event = eventService.getEventByCode(request.getEventCode());
        
        if (!event.getIsActive()) {
            throw new RuntimeException("Event is not active");
        }
        
        if (event.getIsExpired()) {
            throw new RuntimeException("Event has expired");
        }
        
        Guest guest = new Guest();
        guest.setName(request.getName());
        guest.setEmail(request.getEmail());
        guest.setPhoneNumber(request.getPhoneNumber());
        guest.setEvent(event);
        guest.setUploadCount(0);
        
        guest = guestRepository.save(guest);
        
        return convertToResponse(guest);
    }
    
    public Guest getGuestById(Long guestId) {
        return guestRepository.findById(guestId)
                .orElseThrow(() -> new RuntimeException("Guest not found"));
    }
    
    public List<GuestResponse> getEventGuests(Long eventId) {
        return guestRepository.findByEventId(eventId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void incrementUploadCount(Guest guest) {
        guest.setUploadCount(guest.getUploadCount() + 1);
        guestRepository.save(guest);
    }
    
    private GuestResponse convertToResponse(Guest guest) {
        return GuestResponse.builder()
                .id(guest.getId())
                .name(guest.getName())
                .email(guest.getEmail())
                .phoneNumber(guest.getPhoneNumber())
                .uploadCount(guest.getUploadCount())
                .createdAt(guest.getCreatedAt())
                .build();
    }
}
