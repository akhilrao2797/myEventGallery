package com.example.eventphoto.repository;

import com.example.eventphoto.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest, Long> {
    List<Guest> findByEventId(Long eventId);
    Optional<Guest> findByEmailAndEventId(String email, Long eventId);
    List<Guest> findByEmail(String email);

    @org.springframework.data.jpa.repository.Query("SELECT g.event FROM Guest g WHERE g.id = :guestId")
    List<com.example.eventphoto.model.Event> findEventsByGuestId(Long guestId);
}
