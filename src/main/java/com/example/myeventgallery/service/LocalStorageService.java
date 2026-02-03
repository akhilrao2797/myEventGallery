package com.example.myeventgallery.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Local file system storage implementation for development
 * Stores files in a local directory instead of S3
 */
@Service
@Profile({"local", "dev", "default"})
public class LocalStorageService implements StorageService {
    
    @Value("${storage.local.base-path:./uploads}")
    private String basePath;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
    
    @Override
    public String uploadFile(MultipartFile file, String folderPath) throws IOException {
        // Create directory if it doesn't exist
        Path uploadPath = Paths.get(basePath, folderPath);
        Files.createDirectories(uploadPath);
        
        // Generate unique filename
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        
        // Copy file to target location
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Return the relative path as key
        return folderPath + fileName;
    }
    
    @Override
    public String getFileUrl(String key) {
        // Return URL to access the file via API
        return baseUrl + "/api/files/" + key;
    }
    
    @Override
    public void deleteFile(String key) {
        try {
            Path filePath = Paths.get(basePath, key);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + key, e);
        }
    }
    
    @Override
    public boolean doesFileExist(String key) {
        Path filePath = Paths.get(basePath, key);
        return Files.exists(filePath);
    }
    
    /**
     * Get file bytes for serving via API
     */
    public byte[] getFile(String key) throws IOException {
        Path filePath = Paths.get(basePath, key);
        if (!Files.exists(filePath)) {
            throw new IOException("File not found: " + key);
        }
        return Files.readAllBytes(filePath);
    }
}
