package com.example.myeventgallery.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "packages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PricingPackage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private PackageType packageType;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "max_guests")
    private Integer maxGuests;
    
    @Column(name = "max_images")
    private Integer maxImages;
    
    @Column(name = "storage_days")
    private Integer storageDays;
    
    @Column(name = "storage_gb")
    private Integer storageGB;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;
    
    @Column(name = "price_per_extra_guest", precision = 10, scale = 2)
    private BigDecimal pricePerExtraGuest;
    
    @Column(name = "price_per_extra_gb", precision = 10, scale = 2)
    private BigDecimal pricePerExtraGB;
    
    @Column(name = "company_profit_percentage", precision = 5, scale = 2)
    private BigDecimal companyProfitPercentage = new BigDecimal("20.00");
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
