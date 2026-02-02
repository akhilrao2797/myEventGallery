package com.example.myeventgallery.service;

import com.example.myeventgallery.dto.PackageResponse;
import com.example.myeventgallery.model.PricingPackage;
import com.example.myeventgallery.model.PackageType;
import com.example.myeventgallery.repository.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PackageService {
    
    @Autowired
    private PackageRepository packageRepository;
    
    public List<PackageResponse> getAllActivePackages() {
        return packageRepository.findByIsActiveTrue().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public PricingPackage getPackageByType(PackageType packageType) {
        return packageRepository.findByPackageType(packageType)
                .orElseThrow(() -> new RuntimeException("Package not found"));
    }
    
    public PricingPackage getPackageById(Long id) {
        return packageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Package not found"));
    }
    
    private PackageResponse convertToResponse(PricingPackage pkg) {
        return PackageResponse.builder()
                .id(pkg.getId())
                .packageType(pkg.getPackageType())
                .name(pkg.getName())
                .description(pkg.getDescription())
                .maxGuests(pkg.getMaxGuests())
                .maxImages(pkg.getMaxImages())
                .storageDays(pkg.getStorageDays())
                .storageGB(pkg.getStorageGB())
                .basePrice(pkg.getBasePrice())
                .pricePerExtraGuest(pkg.getPricePerExtraGuest())
                .pricePerExtraGB(pkg.getPricePerExtraGB())
                .isActive(pkg.getIsActive())
                .build();
    }
}
