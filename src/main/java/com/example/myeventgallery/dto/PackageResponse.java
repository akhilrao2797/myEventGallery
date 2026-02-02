package com.example.myeventgallery.dto;

import com.example.myeventgallery.model.PackageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageResponse {
    private Long id;
    private PackageType packageType;
    private String name;
    private String description;
    private Integer maxGuests;
    private Integer maxImages;
    private Integer storageDays;
    private Integer storageGB;
    private BigDecimal basePrice;
    private BigDecimal pricePerExtraGuest;
    private BigDecimal pricePerExtraGB;
    private Boolean isActive;
}
