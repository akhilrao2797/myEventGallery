package com.example.eventphoto.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Default: allow all. Replace with integration to open-source NSFW detector
 * (e.g. Hugging Face model, or external moderation API).
 */
@Service
public class ContentModerationServiceImpl implements ContentModerationService {

    @Override
    public boolean isSafe(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) return false;
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) return false;
        // Placeholder: accept all. Integrate with model/API for production.
        return true;
    }
}
