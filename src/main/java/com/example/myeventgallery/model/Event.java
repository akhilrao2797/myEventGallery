package com.example.myeventgallery.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, updatable = false)
    private String eventCode = UUID.randomUUID().toString();
    
    @NotBlank(message = "Event name is required")
    @Column(nullable = false)
    private String name;
    
    @NotNull(message = "Event type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotNull(message = "Event date is required")
    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;
    
    @Column(name = "event_start_time")
    private LocalTime eventStartTime;
    
    @Column(name = "event_end_time")
    private LocalTime eventEndTime;
    
    @Column(name = "qr_valid_until")
    private LocalDateTime qrValidUntil;
    
    @Column(name = "venue")
    private String venue;
    
    @Column(name = "expected_guests")
    private Integer expectedGuests;
    
    @Column(name = "qr_code_url")
    private String qrCodeUrl;
    
    @Column(name = "s3_folder_path")
    private String s3FolderPath;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private PricingPackage packagePlan;
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Guest> guests = new ArrayList<>();
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Image> images = new ArrayList<>();
    
    @OneToOne(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "is_expired")
    private Boolean isExpired = false;
    
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    
    @Column(name = "total_uploads")
    private Integer totalUploads = 0;
    
    @Column(name = "total_size_mb")
    private Double totalSizeMB = 0.0;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    public void prePersist() {
        if (this.eventCode == null) {
            this.eventCode = UUID.randomUUID().toString();
        }
        if (this.s3FolderPath == null) {
            this.s3FolderPath = "events/" + this.eventCode + "/";
        }
        if (this.qrValidUntil == null && this.eventDate != null) {
            // QR valid until 3 days after event date at 23:59:59
            this.qrValidUntil = this.eventDate.plusDays(3).atTime(23, 59, 59);
        }
    }
    
    /**
     * Check if QR code is currently valid for uploads
     */
    public boolean isQrCodeValid() {
        if (qrValidUntil == null) {
            return false; // No validity set means invalid
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime eventStart = (eventStartTime != null) 
            ? eventDate.atTime(eventStartTime)
            : eventDate.atStartOfDay();
        
        return now.isAfter(eventStart) && now.isBefore(qrValidUntil);
    }
}
