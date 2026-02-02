package com.example.myeventgallery.repository;

import com.example.myeventgallery.model.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByEventId(Long eventId);
    Page<Image> findByEventId(Long eventId, Pageable pageable);
    List<Image> findByEventEventCode(String eventCode);
    Page<Image> findByEventEventCode(String eventCode, Pageable pageable);
    List<Image> findByGuestId(Long guestId);
    Long countByEventId(Long eventId);
    
    @Query("SELECT SUM(i.fileSizeMB) FROM Image i WHERE i.event.id = :eventId")
    Double getTotalSizeByEventId(Long eventId);
}
