package com.example.eventphoto.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Interface for content moderation (NSFW/inappropriate image detection).
 * Implement with open-source model (e.g. Hugging Face-based) or external API.
 */
public interface ContentModerationService {
    /**
     * @return true if image is safe, false if inappropriate (reject upload).
     */
    boolean isSafe(MultipartFile file) throws Exception;
}
