package com.example.eventphoto.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * Perceptual hashing for duplicate/near-duplicate image detection.
 */
public interface DuplicateDetectionService {
    /**
     * Compute perceptual hash for image (e.g. pHash/DCT-based).
     */
    String computeHash(MultipartFile file) throws Exception;

    /**
     * Check if this hash is duplicate of any existing in event for this guest.
     * @return optional of existing image id if duplicate found
     */
    Optional<Long> findDuplicate(Long eventId, Long guestId, String perceptualHash);
}
