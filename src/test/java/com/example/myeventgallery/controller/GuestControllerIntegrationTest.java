package com.example.myeventgallery.controller;

import com.example.myeventgallery.dto.EventCreateRequest;
import com.example.myeventgallery.dto.GuestRegistrationRequest;
import com.example.myeventgallery.model.EventType;
import com.example.myeventgallery.model.PackageType;
import com.example.myeventgallery.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GuestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void testRegisterGuest_Success() throws Exception {
        // First create an event
        String customerToken = jwtUtil.generateToken("customer@example.com", 1L);
        
        EventCreateRequest eventRequest = new EventCreateRequest();
        eventRequest.setName("Test Event");
        eventRequest.setEventType(EventType.MARRIAGE);
        eventRequest.setEventDate(LocalDate.now().plusDays(30));
        eventRequest.setVenue("Test Venue");
        eventRequest.setExpectedGuests(100);
        eventRequest.setPackageType(PackageType.BASIC);

        String createResponse = mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventRequest)))
                .andReturn().getResponse().getContentAsString();

        String eventCode = objectMapper.readTree(createResponse).get("data").get("eventCode").asText();

        // Register guest
        GuestRegistrationRequest guestRequest = new GuestRegistrationRequest();
        guestRequest.setName("Guest User");
        guestRequest.setEmail("guest@example.com");
        guestRequest.setPhoneNumber("1234567890");
        guestRequest.setEventCode(eventCode);

        mockMvc.perform(post("/api/guest/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(guestRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.guestId").exists());
    }

    @Test
    void testRegisterGuest_InvalidEventCode() throws Exception {
        GuestRegistrationRequest guestRequest = new GuestRegistrationRequest();
        guestRequest.setName("Guest User");
        guestRequest.setEmail("guest@example.com");
        guestRequest.setPhoneNumber("1234567890");
        guestRequest.setEventCode("INVALID");

        mockMvc.perform(post("/api/guest/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(guestRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testUploadImages_Success() throws Exception {
        // Setup: Create event and register guest
        String customerToken = jwtUtil.generateToken("customer@example.com", 1L);
        
        EventCreateRequest eventRequest = new EventCreateRequest();
        eventRequest.setName("Test Event");
        eventRequest.setEventType(EventType.MARRIAGE);
        eventRequest.setEventDate(LocalDate.now().plusDays(1));
        eventRequest.setVenue("Test Venue");
        eventRequest.setExpectedGuests(100);
        eventRequest.setPackageType(PackageType.BASIC);

        String createResponse = mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventRequest)))
                .andReturn().getResponse().getContentAsString();

        String eventCode = objectMapper.readTree(createResponse).get("data").get("eventCode").asText();

        GuestRegistrationRequest guestRequest = new GuestRegistrationRequest();
        guestRequest.setName("Guest User");
        guestRequest.setEmail("guest@example.com");
        guestRequest.setPhoneNumber("1234567890");
        guestRequest.setEventCode(eventCode);

        String guestResponse = mockMvc.perform(post("/api/guest/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(guestRequest)))
                .andReturn().getResponse().getContentAsString();

        Long guestId = objectMapper.readTree(guestResponse).get("data").get("guestId").asLong();

        // Upload images
        MockMultipartFile file = new MockMultipartFile(
            "images",
            "test.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "test image content".getBytes()
        );

        mockMvc.perform(multipart("/api/guest/" + guestId + "/upload")
                .file(file)
                .param("eventCode", eventCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testUploadImages_QRCodeExpired() throws Exception {
        // Create event with past date
        String customerToken = jwtUtil.generateToken("customer@example.com", 1L);
        
        EventCreateRequest eventRequest = new EventCreateRequest();
        eventRequest.setName("Test Event");
        eventRequest.setEventType(EventType.MARRIAGE);
        eventRequest.setEventDate(LocalDate.now().minusDays(10)); // Past event
        eventRequest.setVenue("Test Venue");
        eventRequest.setExpectedGuests(100);
        eventRequest.setPackageType(PackageType.BASIC);

        String createResponse = mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventRequest)))
                .andReturn().getResponse().getContentAsString();

        String eventCode = objectMapper.readTree(createResponse).get("data").get("eventCode").asText();

        GuestRegistrationRequest guestRequest = new GuestRegistrationRequest();
        guestRequest.setName("Guest User");
        guestRequest.setEmail("guest@example.com");
        guestRequest.setPhoneNumber("1234567890");
        guestRequest.setEventCode(eventCode);

        String guestResponse = mockMvc.perform(post("/api/guest/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(guestRequest)))
                .andReturn().getResponse().getContentAsString();

        Long guestId = objectMapper.readTree(guestResponse).get("data").get("guestId").asLong();

        // Try to upload - should fail due to expired QR
        MockMultipartFile file = new MockMultipartFile(
            "images",
            "test.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "test image content".getBytes()
        );

        mockMvc.perform(multipart("/api/guest/" + guestId + "/upload")
                .file(file)
                .param("eventCode", eventCode))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("QR code")));
    }
}
