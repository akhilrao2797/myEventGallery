package com.example.myeventgallery.repository;

import com.example.myeventgallery.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {
    
    List<Guest> findByEventId(Long eventId);
    
    List<Guest> findByEventEventCode(String eventCode);
    
    Optional<Guest> findByEmailAndEventEventCode(String email, String eventCode);
    
    Optional<Guest> findByEmail(String email);
    
    boolean existsByEmailAndEventId(String email, Long eventId);
}
