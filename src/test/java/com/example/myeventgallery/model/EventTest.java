package com.example.myeventgallery.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Test
    void testPrePersistGeneratesEventCode() {
        Event event = new Event();
        event.prePersist();

        assertNotNull(event.getEventCode());
        assertNotNull(event.getS3FolderPath());
        assertTrue(event.getS3FolderPath().startsWith("events/"));
        assertTrue(event.getS3FolderPath().endsWith("/"));
    }

    @Test
    void testEventCodeIsUnique() {
        Event event1 = new Event();
        event1.prePersist();

        Event event2 = new Event();
        event2.prePersist();

        assertNotEquals(event1.getEventCode(), event2.getEventCode());
    }

    @Test
    void testEventDefaultValues() {
        Event event = new Event();

        assertEquals(0, event.getTotalUploads());
        assertEquals(0.0, event.getTotalSizeMB());
    }

    @Test
    void testS3FolderPathFormat() {
        Event event = new Event();
        event.prePersist();

        String expectedPattern = "events/.*/";
        assertTrue(event.getS3FolderPath().matches(expectedPattern));
    }
}
