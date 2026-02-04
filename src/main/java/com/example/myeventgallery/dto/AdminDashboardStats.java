package com.example.myeventgallery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardStats {
    // Overall stats
    private Long totalCustomers;
    private Long totalEvents;
    private Long totalGuests;
    private Long totalImages;
    private Double totalStorageGB;
    
    // Recent activity
    private Long eventsToday;
    private Long eventsThisWeek;
    private Long eventsThisMonth;
    
    // Revenue stats (if applicable)
    private Double totalRevenue;
    private Double revenueThisMonth;
    
    // Package distribution
    private Map<String, Long> packageDistribution;
    
    // Event type distribution
    private Map<String, Long> eventTypeDistribution;
    
    // Active vs Expired
    private Long activeEvents;
    private Long expiredEvents;
    
    // Storage stats
    private Double averageStoragePerEvent;
    private Long eventsNearStorageLimit;
    
    // System health
    private String systemStatus;
    private LocalDateTime lastUpdated;
}
