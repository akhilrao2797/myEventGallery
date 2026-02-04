package com.example.myeventgallery.repository;

import com.example.myeventgallery.model.SharedFolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SharedFolderRepository extends JpaRepository<SharedFolder, Long> {
    
    Optional<SharedFolder> findByShareCode(String shareCode);
    
    List<SharedFolder> findByCustomerId(Long customerId);
    
    List<SharedFolder> findByEventId(Long eventId);
    
    List<SharedFolder> findByEventIdAndCustomerId(Long eventId, Long customerId);
}
