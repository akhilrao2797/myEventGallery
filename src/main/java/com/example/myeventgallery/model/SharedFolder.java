package com.example.myeventgallery.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Shared folders that customers can create to organize and share images
 */
@Entity
@Table(name = "shared_folders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedFolder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "folder_name", nullable = false)
    private String folderName;
    
    @Column(name = "share_code", nullable = false, unique = true, length = 100)
    private String shareCode;
    
    @Column(name = "access_password")
    private String accessPassword;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @ManyToMany
    @JoinTable(
        name = "folder_images",
        joinColumns = @JoinColumn(name = "folder_id"),
        inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    private List<Image> images = new ArrayList<>();
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;
    
    @Column(name = "download_count")
    private Integer downloadCount = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    public void generateShareCode() {
        if (this.shareCode == null) {
            this.shareCode = UUID.randomUUID().toString().substring(0, 12);
        }
    }
}
