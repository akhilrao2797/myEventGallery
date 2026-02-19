package com.example.eventphoto.service;

import com.example.eventphoto.model.AppProperty;
import com.example.eventphoto.repository.AppPropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppPropertyService {

    public static final String GUEST_MODIFY_DAYS_AFTER_EVENT = "guest.modify.days.after.event";
    public static final String GUEST_UPLOAD_MAX_IMAGES_PER_BATCH = "guest.upload.max.images.per.batch";

    private final AppPropertyRepository repository;

    public int getGuestModifyDaysAfterEvent() {
        return getInt(GUEST_MODIFY_DAYS_AFTER_EVENT, 1);
    }

    public int getGuestUploadMaxImagesPerBatch() {
        return getInt(GUEST_UPLOAD_MAX_IMAGES_PER_BATCH, 20);
    }

    private int getInt(String key, int defaultValue) {
        return repository.findByPropertyKey(key)
                .map(AppProperty::getPropertyValue)
                .map(s -> {
                    try {
                        return Integer.parseInt(s.trim());
                    } catch (NumberFormatException e) {
                        return defaultValue;
                    }
                })
                .orElse(defaultValue);
    }
}
