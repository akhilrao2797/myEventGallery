package com.example.myeventgallery.controller;

import com.example.myeventgallery.dto.*;
import com.example.myeventgallery.model.Customer;
import com.example.myeventgallery.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> adminLogin(@RequestBody AdminLoginRequest request) {
        try {
            AuthResponse response = adminService.adminLogin(request);
            return ResponseEntity.ok(ApiResponse.success("Admin login successful", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse<AdminDashboardStats>> getDashboardStats() {
        try {
            AdminDashboardStats stats = adminService.getDashboardStats();
            return ResponseEntity.ok(ApiResponse.success("Stats retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/events")
    public ResponseEntity<ApiResponse<Page<AdminEventResponse>>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<AdminEventResponse> events = adminService.getAllEvents(pageable);
            return ResponseEntity.ok(ApiResponse.success("Events retrieved successfully", events));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/events/search")
    public ResponseEntity<ApiResponse<List<AdminEventResponse>>> searchEvents(@RequestParam String query) {
        try {
            List<AdminEventResponse> events = adminService.searchEvents(query);
            return ResponseEntity.ok(ApiResponse.success("Search completed", events));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable Long eventId) {
        try {
            adminService.deleteEvent(eventId);
            return ResponseEntity.ok(ApiResponse.success("Event deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/events/{eventId}")
    public ResponseEntity<ApiResponse<AdminEventResponse>> updateEvent(
            @PathVariable Long eventId,
            @RequestBody Map<String, Object> updates) {
        try {
            AdminEventResponse event = adminService.updateEvent(eventId, updates);
            return ResponseEntity.ok(ApiResponse.success("Event updated successfully", event));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/customers")
    public ResponseEntity<ApiResponse<List<Customer>>> getAllCustomers() {
        try {
            List<Customer> customers = adminService.getAllCustomers();
            return ResponseEntity.ok(ApiResponse.success("Customers retrieved successfully", customers));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/customers/{customerId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCustomerDetails(@PathVariable Long customerId) {
        try {
            Map<String, Object> details = adminService.getCustomerDetails(customerId);
            return ResponseEntity.ok(ApiResponse.success("Customer details retrieved", details));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/customers/{customerId}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long customerId) {
        try {
            adminService.deleteCustomer(customerId);
            return ResponseEntity.ok(ApiResponse.success("Customer deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
