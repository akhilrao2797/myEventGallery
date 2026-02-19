package com.example.eventphoto.repository;

import com.example.eventphoto.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByEventCode(String eventCode);
    List<Event> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    boolean existsByEventCode(String eventCode);
}
