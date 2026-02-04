package com.example.myeventgallery.controller;

import com.example.myeventgallery.dto.AdminLoginRequest;
import com.example.myeventgallery.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void testAdminLogin_Success() throws Exception {
        AdminLoginRequest request = new AdminLoginRequest();
        request.setUsername("admin");
        request.setPassword("Admin@123");

        mockMvc.perform(post("/api/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.adminId").exists())
                .andExpect(jsonPath("$.data.role").exists());
    }

    @Test
    void testAdminLogin_InvalidCredentials() throws Exception {
        AdminLoginRequest request = new AdminLoginRequest();
        request.setUsername("admin");
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/api/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testGetAdminDashboard_Success() throws Exception {
        // Login first
        AdminLoginRequest loginRequest = new AdminLoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("Admin@123");

        String loginResponse = mockMvc.perform(post("/api/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginResponse).get("data").get("token").asText();

        // Get dashboard stats
        mockMvc.perform(get("/api/admin/dashboard/stats")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCustomers").exists())
                .andExpect(jsonPath("$.data.totalEvents").exists())
                .andExpect(jsonPath("$.data.totalImages").exists())
                .andExpect(jsonPath("$.data.totalRevenue").exists());
    }

    @Test
    void testGetAdminDashboard_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/stats"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetAllEvents_Success() throws Exception {
        // Login as admin
        AdminLoginRequest loginRequest = new AdminLoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("Admin@123");

        String loginResponse = mockMvc.perform(post("/api/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginResponse).get("data").get("token").asText();

        // Get all events
        mockMvc.perform(get("/api/admin/events")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testSearchEvents_Success() throws Exception {
        // Login as admin
        AdminLoginRequest loginRequest = new AdminLoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("Admin@123");

        String loginResponse = mockMvc.perform(post("/api/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginResponse).get("data").get("token").asText();

        // Search events
        mockMvc.perform(get("/api/admin/events/search")
                .header("Authorization", "Bearer " + token)
                .param("query", "wedding"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetAllCustomers_Success() throws Exception {
        // Login as admin
        AdminLoginRequest loginRequest = new AdminLoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("Admin@123");

        String loginResponse = mockMvc.perform(post("/api/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginResponse).get("data").get("token").asText();

        // Get all customers
        mockMvc.perform(get("/api/admin/customers")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }
}
