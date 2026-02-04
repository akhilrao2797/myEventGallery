package com.example.myeventgallery.service;

import com.example.myeventgallery.dto.*;
import com.example.myeventgallery.model.*;
import com.example.myeventgallery.repository.*;
import com.example.myeventgallery.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private GuestRepository guestRepository;
    
    @Autowired
    private ImageRepository imageRepository;
    
    @Autowired
    private PackageRepository packageRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public AuthResponse adminLogin(AdminLoginRequest request) {
        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        if (!admin.getIsActive()) {
            throw new RuntimeException("Admin account is inactive");
        }
        
        // Generate token (reusing customer token generation for simplicity)
        String token = jwtUtil.generateCustomerToken(admin.getEmail(), admin.getId());
        
        return new AuthResponse(token, admin.getId(), admin.getFullName(), admin.getEmail());
    }
    
    @Transactional
    public AuthResponse registerAdmin(AdminRegistrationRequest request) {
        // Validate super admin key for security (only super admins can create new admins)
        String expectedKey = "SUPER_ADMIN_SECRET_KEY_2026"; // In production, use environment variable
        if (request.getSuperAdminKey() == null || !request.getSuperAdminKey().equals(expectedKey)) {
            throw new RuntimeException("Invalid super admin key. Only authorized super admins can create admin accounts.");
        }
        
        // Check if username already exists
        if (adminRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        
        // Check if email already exists
        if (adminRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        
        // Create new admin
        Admin admin = new Admin();
        admin.setUsername(request.getUsername());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setFullName(request.getFullName());
        admin.setRole(request.getRole());
        admin.setIsActive(true);
        admin.setCreatedAt(LocalDateTime.now());
        
        admin = adminRepository.save(admin);
        
        // Generate token
        String token = jwtUtil.generateCustomerToken(admin.getEmail(), admin.getId());
        
        return new AuthResponse(token, admin.getId(), admin.getFullName(), admin.getEmail());
    }
    
    public AdminDashboardStats getDashboardStats() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(7);
        LocalDate monthStart = today.withDayOfMonth(1);
        
        // Get counts
        Long totalCustomers = customerRepository.count();
        Long totalEvents = eventRepository.count();
        Long totalGuests = guestRepository.count();
        Long totalImages = imageRepository.count();
        
        // Calculate storage
        Double totalStorageMB = imageRepository.findAll().stream()
                .mapToDouble(img -> img.getFileSizeMB() != null ? img.getFileSizeMB() : 0.0)
                .sum();
        Double totalStorageGB = totalStorageMB / 1024.0;
        
        // Recent events
        Long eventsToday = eventRepository.findAll().stream()
                .filter(e -> e.getEventDate().equals(today))
                .count();
        
        Long eventsThisWeek = eventRepository.findAll().stream()
                .filter(e -> !e.getEventDate().isBefore(weekStart))
                .count();
        
        Long eventsThisMonth = eventRepository.findAll().stream()
                .filter(e -> !e.getEventDate().isBefore(monthStart))
                .count();
        
        // Package distribution
        Map<String, Long> packageDist = eventRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                    e -> e.getPackagePlan().getName(),
                    Collectors.counting()
                ));
        
        // Event type distribution
        Map<String, Long> eventTypeDist = eventRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                    e -> e.getEventType().toString(),
                    Collectors.counting()
                ));
        
        // Active vs Expired
        Long activeEvents = eventRepository.findAll().stream()
                .filter(e -> !e.getIsExpired())
                .count();
        
        Long expiredEvents = eventRepository.findAll().stream()
                .filter(Event::getIsExpired)
                .count();
        
        // Average storage
        Double avgStorage = totalEvents > 0 ? totalStorageMB / totalEvents : 0.0;
        
        return AdminDashboardStats.builder()
                .totalCustomers(totalCustomers)
                .totalEvents(totalEvents)
                .totalGuests(totalGuests)
                .totalImages(totalImages)
                .totalStorageGB(totalStorageGB)
                .eventsToday(eventsToday)
                .eventsThisWeek(eventsThisWeek)
                .eventsThisMonth(eventsThisMonth)
                .packageDistribution(packageDist)
                .eventTypeDistribution(eventTypeDist)
                .activeEvents(activeEvents)
                .expiredEvents(expiredEvents)
                .averageStoragePerEvent(avgStorage)
                .systemStatus("HEALTHY")
                .lastUpdated(LocalDateTime.now())
                .build();
    }
    
    public Page<AdminEventResponse> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable)
                .map(this::convertToAdminEventResponse);
    }
    
    public List<AdminEventResponse> searchEvents(String query) {
        return eventRepository.findAll().stream()
                .filter(e -> e.getName().toLowerCase().contains(query.toLowerCase()) ||
                           e.getEventCode().toLowerCase().contains(query.toLowerCase()) ||
                           e.getCustomer().getName().toLowerCase().contains(query.toLowerCase()))
                .map(this::convertToAdminEventResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        eventRepository.delete(event);
    }
    
    @Transactional
    public void deleteCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customerRepository.delete(customer);
    }
    
    @Transactional
    public AdminEventResponse updateEvent(Long eventId, Map<String, Object> updates) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        // Update allowed fields
        if (updates.containsKey("isActive")) {
            event.setIsActive((Boolean) updates.get("isActive"));
        }
        if (updates.containsKey("isExpired")) {
            event.setIsExpired((Boolean) updates.get("isExpired"));
        }
        if (updates.containsKey("name")) {
            event.setName((String) updates.get("name"));
        }
        
        event = eventRepository.save(event);
        return convertToAdminEventResponse(event);
    }
    
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
    
    public Map<String, Object> getCustomerDetails(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        List<Event> events = eventRepository.findByCustomerId(customerId);
        
        Map<String, Object> details = new HashMap<>();
        details.put("customer", customer);
        details.put("totalEvents", events.size());
        details.put("events", events.stream()
                .map(this::convertToAdminEventResponse)
                .collect(Collectors.toList()));
        
        return details;
    }
    
    private AdminEventResponse convertToAdminEventResponse(Event event) {
        return AdminEventResponse.builder()
                .id(event.getId())
                .eventCode(event.getEventCode())
                .name(event.getName())
                .eventType(event.getEventType())
                .eventDate(event.getEventDate())
                .eventStartTime(event.getEventStartTime())
                .eventEndTime(event.getEventEndTime())
                .venue(event.getVenue())
                .expectedGuests(event.getExpectedGuests())
                .qrCodeUrl(event.getQrCodeUrl())
                .customerId(event.getCustomer().getId())
                .customerName(event.getCustomer().getName())
                .customerEmail(event.getCustomer().getEmail())
                .packageName(event.getPackagePlan().getName())
                .packagePrice(event.getPackagePlan().getBasePrice().doubleValue())
                .guestCount(event.getGuests().size())
                .totalUploads(event.getTotalUploads() != null ? event.getTotalUploads().longValue() : 0L)
                .totalSizeMB(event.getTotalSizeMB())
                .isActive(event.getIsActive())
                .isExpired(event.getIsExpired())
                .qrValidUntil(event.getQrValidUntil())
                .qrCodeValid(event.isQrCodeValid())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
