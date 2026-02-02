package com.example.myeventgallery.service;

import com.example.myeventgallery.dto.PackageResponse;
import com.example.myeventgallery.model.PackageType;
import com.example.myeventgallery.model.PricingPackage;
import com.example.myeventgallery.repository.PackageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PackageServiceTest {

    @Mock
    private PackageRepository packageRepository;

    @InjectMocks
    private PackageService packageService;

    private PricingPackage basicPackage;
    private PricingPackage premiumPackage;

    @BeforeEach
    void setUp() {
        basicPackage = new PricingPackage();
        basicPackage.setId(1L);
        basicPackage.setPackageType(PackageType.BASIC);
        basicPackage.setName("Basic Package");
        basicPackage.setMaxGuests(50);
        basicPackage.setMaxImages(500);
        basicPackage.setStorageDays(30);
        basicPackage.setStorageGB(5);
        basicPackage.setBasePrice(new BigDecimal("29.99"));
        basicPackage.setIsActive(true);

        premiumPackage = new PricingPackage();
        premiumPackage.setId(2L);
        premiumPackage.setPackageType(PackageType.PREMIUM);
        premiumPackage.setName("Premium Package");
        premiumPackage.setMaxGuests(300);
        premiumPackage.setMaxImages(5000);
        premiumPackage.setStorageDays(180);
        premiumPackage.setStorageGB(50);
        premiumPackage.setBasePrice(new BigDecimal("149.99"));
        premiumPackage.setIsActive(true);
    }

    @Test
    void testGetAllActivePackages() {
        List<PricingPackage> packages = Arrays.asList(basicPackage, premiumPackage);
        when(packageRepository.findByIsActiveTrue()).thenReturn(packages);

        List<PackageResponse> responses = packageService.getAllActivePackages();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Basic Package", responses.get(0).getName());
        assertEquals("Premium Package", responses.get(1).getName());

        verify(packageRepository).findByIsActiveTrue();
    }

    @Test
    void testGetPackageByType() {
        when(packageRepository.findByPackageType(PackageType.BASIC)).thenReturn(Optional.of(basicPackage));

        PricingPackage result = packageService.getPackageByType(PackageType.BASIC);

        assertNotNull(result);
        assertEquals(PackageType.BASIC, result.getPackageType());
        assertEquals("Basic Package", result.getName());
        assertEquals(50, result.getMaxGuests());

        verify(packageRepository).findByPackageType(PackageType.BASIC);
    }

    @Test
    void testGetPackageByTypeNotFound() {
        when(packageRepository.findByPackageType(PackageType.ENTERPRISE)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            packageService.getPackageByType(PackageType.ENTERPRISE);
        });

        assertEquals("Package not found", exception.getMessage());
    }

    @Test
    void testGetPackageById() {
        when(packageRepository.findById(1L)).thenReturn(Optional.of(basicPackage));

        PricingPackage result = packageService.getPackageById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Basic Package", result.getName());

        verify(packageRepository).findById(1L);
    }

    @Test
    void testGetPackageByIdNotFound() {
        when(packageRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            packageService.getPackageById(999L);
        });

        assertEquals("Package not found", exception.getMessage());
    }
}
