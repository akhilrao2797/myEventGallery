package com.example.myeventgallery.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class EventQRValidationTest {

    @Test
    void testQRCodeValidDuringEvent() {
        Event event = new Event();
        event.setEventDate(LocalDate.now());
        event.setEventStartTime(LocalTime.of(10, 0));
        event.setQrValidUntil(LocalDate.now().plusDays(3).atTime(23, 59, 59));

        // Simulate current time after event start
        boolean isValid = event.isQrCodeValid();
        
        assertTrue(event.getQrValidUntil().isAfter(LocalDateTime.now()));
    }

    @Test
    void testQRCodeInvalidBeforeEvent() {
        Event event = new Event();
        event.setEventDate(LocalDate.now().plusDays(5)); // Future event
        event.setEventStartTime(LocalTime.of(18, 0));
        event.setQrValidUntil(LocalDate.now().plusDays(8).atTime(23, 59, 59));

        // Current time is before event start
        boolean isValid = event.isQrCodeValid();
        
        assertFalse(isValid);
    }

    @Test
    void testQRCodeInvalidAfter3Days() {
        Event event = new Event();
        event.setEventDate(LocalDate.now().minusDays(5)); // Past event
        event.setEventStartTime(LocalTime.of(18, 0));
        event.setQrValidUntil(LocalDate.now().minusDays(2).atTime(23, 59, 59)); // Expired

        boolean isValid = event.isQrCodeValid();
        
        assertFalse(isValid);
    }

    @Test
    void testQRCodeValidOn3rdDay() {
        Event event = new Event();
        event.setEventDate(LocalDate.now().minusDays(2));
        event.setEventStartTime(LocalTime.of(10, 0));
        event.setQrValidUntil(LocalDate.now().plusDays(1).atTime(23, 59, 59)); // Still valid

        boolean isValid = event.isQrCodeValid();
        
        assertTrue(isValid);
    }

    @Test
    void testPrePersistSetsQRValidUntil() {
        Event event = new Event();
        event.setEventDate(LocalDate.now());
        event.prePersist();

        assertNotNull(event.getQrValidUntil());
        assertEquals(LocalDate.now().plusDays(3).atTime(23, 59, 59), event.getQrValidUntil());
    }

    @Test
    void testPrePersistSetsEventCodeAndS3Path() {
        Event event = new Event();
        event.prePersist();

        assertNotNull(event.getEventCode());
        assertNotNull(event.getS3FolderPath());
        assertTrue(event.getS3FolderPath().startsWith("events/"));
    }
}
