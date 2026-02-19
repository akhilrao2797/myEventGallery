package com.example.eventphoto.repository;

import com.example.eventphoto.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByEventId(Long eventId);
    List<Image> findByEventIdAndGuestId(Long eventId, Long guestId);
    List<Image> findByIdIn(List<Long> ids);
    Optional<Image> findByEventIdAndGuestIdAndPerceptualHash(Long eventId, Long guestId, String hash);
}
