package com.example.myeventgallery.controller;

import com.example.myeventgallery.dto.ApiResponse;
import com.example.myeventgallery.dto.PackageResponse;
import com.example.myeventgallery.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
public class PackageController {
    
    @Autowired
    private PackageService packageService;
    
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<PackageResponse>>> getAllPackages() {
        try {
            List<PackageResponse> packages = packageService.getAllActivePackages();
            return ResponseEntity.ok(ApiResponse.success("Packages retrieved successfully", packages));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
