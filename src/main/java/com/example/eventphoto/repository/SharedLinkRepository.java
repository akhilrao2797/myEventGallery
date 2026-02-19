package com.example.eventphoto.repository;

import com.example.eventphoto.model.SharedLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import java.util.List;

public interface SharedLinkRepository extends JpaRepository<SharedLink, Long> {
    Optional<SharedLink> findByShareCode(String shareCode);
    List<SharedLink> findByCustomerId(Long customerId);
}
