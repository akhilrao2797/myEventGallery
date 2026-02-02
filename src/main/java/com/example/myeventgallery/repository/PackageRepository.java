package com.example.myeventgallery.repository;

import com.example.myeventgallery.model.PricingPackage;
import com.example.myeventgallery.model.PackageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PackageRepository extends JpaRepository<PricingPackage, Long> {
    Optional<PricingPackage> findByPackageType(PackageType packageType);
    List<PricingPackage> findByIsActiveTrue();
}
