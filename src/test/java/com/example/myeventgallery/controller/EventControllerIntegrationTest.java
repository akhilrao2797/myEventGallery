package com.example.myeventgallery.controller;

import com.example.myeventgallery.dto.EventCreateRequest;
import com.example.myeventgallery.model.EventType;
import com.example.myeventgallery.model.PackageType;
import com.example.myeventgallery.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void testCreateEvent_Success() throws Exception {
        String token = jwtUtil.generateToken("test@example.com", 1L);
        
        EventCreateRequest request = new EventCreateRequest();
        request.setName("Test Event");
        request.setEventType(EventType.MARRIAGE);
        request.setEventDate(LocalDate.now().plusDays(30));
        request.setVenue("Test Venue");
        request.setExpectedGuests(100);
        request.setPackageType(PackageType.BASIC);

        mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Test Event"))
                .andExpect(jsonPath("$.data.eventCode").exists());
    }

    @Test
    void testCreateEvent_Unauthorized() throws Exception {
        EventCreateRequest request = new EventCreateRequest();
        request.setName("Test Event");
        request.setEventType(EventType.MARRIAGE);
        request.setEventDate(LocalDate.now().plusDays(30));
        request.setVenue("Test Venue");
        request.setExpectedGuests(100);
        request.setPackageType(PackageType.BASIC);

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateEvent_InvalidData() throws Exception {
        String token = jwtUtil.generateToken("test@example.com", 1L);
        
        EventCreateRequest request = new EventCreateRequest();
        // Missing required fields

        mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetMyEvents_Success() throws Exception {
        String token = jwtUtil.generateToken("test@example.com", 1L);

        mockMvc.perform(get("/api/events")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetEventById_Success() throws Exception {
        String token = jwtUtil.generateToken("test@example.com", 1L);

        // First create an event
        EventCreateRequest request = new EventCreateRequest();
        request.setName("Test Event");
        request.setEventType(EventType.MARRIAGE);
        request.setEventDate(LocalDate.now().plusDays(30));
        request.setVenue("Test Venue");
        request.setExpectedGuests(100);
        request.setPackageType(PackageType.BASIC);

        String createResponse = mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        // Extract event ID from response
        Long eventId = objectMapper.readTree(createResponse).get("data").get("id").asLong();

        // Get the event
        mockMvc.perform(get("/api/events/" + eventId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(eventId))
                .andExpect(jsonPath("$.data.name").value("Test Event"));
    }

    @Test
    void testGetQRCode_Success() throws Exception {
        String token = jwtUtil.generateToken("test@example.com", 1L);

        // First create an event
        EventCreateRequest request = new EventCreateRequest();
        request.setName("Test Event");
        request.setEventType(EventType.MARRIAGE);
        request.setEventDate(LocalDate.now().plusDays(30));
        request.setVenue("Test Venue");
        request.setExpectedGuests(100);
        request.setPackageType(PackageType.BASIC);

        String createResponse = mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        String eventCode = objectMapper.readTree(createResponse).get("data").get("eventCode").asText();

        // Get QR code
        mockMvc.perform(get("/api/events/qr/" + eventCode))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG));
    }

    @Test
    void testGetQRCode_NotFound() throws Exception {
        mockMvc.perform(get("/api/events/qr/NONEXISTENT"))
                .andExpect(status().isNotFound());
    }
}
