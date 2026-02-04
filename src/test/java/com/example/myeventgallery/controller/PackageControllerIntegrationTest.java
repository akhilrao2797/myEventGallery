package com.example.myeventgallery.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PackageControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetAllPackages_Success() throws Exception {
        mockMvc.perform(get("/api/packages/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].packageType").exists())
                .andExpect(jsonPath("$.data[0].name").exists())
                .andExpect(jsonPath("$.data[0].basePrice").exists())
                .andExpect(jsonPath("$.data[0].storageGB").exists());
    }

    @Test
    void testGetAllPackages_ReturnsActivePackagesOnly() throws Exception {
        mockMvc.perform(get("/api/packages/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[*].isActive").value(org.hamcrest.Matchers.everyItem(org.hamcrest.Matchers.is(true))));
    }

    @Test
    void testGetAllPackages_ContainsRequiredFields() throws Exception {
        mockMvc.perform(get("/api/packages/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].packageType").exists())
                .andExpect(jsonPath("$.data[0].name").exists())
                .andExpect(jsonPath("$.data[0].description").exists())
                .andExpect(jsonPath("$.data[0].maxGuests").exists())
                .andExpect(jsonPath("$.data[0].maxImages").exists())
                .andExpect(jsonPath("$.data[0].storageDays").exists())
                .andExpect(jsonPath("$.data[0].storageGB").exists())
                .andExpect(jsonPath("$.data[0].basePrice").exists());
    }
}
