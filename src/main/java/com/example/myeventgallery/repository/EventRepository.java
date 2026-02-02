package com.example.myeventgallery.repository;

import com.example.myeventgallery.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByEventCode(String eventCode);
    List<Event> findByCustomerId(Long customerId);
    List<Event> findByIsActiveTrue();
    
    @Query("SELECT e FROM Event e WHERE e.expiryDate < :date AND e.isExpired = false")
    List<Event> findExpiredEvents(LocalDate date);
}
