package com.example.myeventgallery.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Storage service interface to support multiple storage backends
 * (Local file system for dev, S3 for production)
 */
public interface StorageService {
    
    /**
     * Upload a file to storage
     * @param file The file to upload
     * @param folderPath The folder path where to store the file
     * @return The storage key/path of the uploaded file
     */
    String uploadFile(MultipartFile file, String folderPath) throws IOException;
    
    /**
     * Get a URL to access the file
     * @param key The storage key/path of the file
     * @return URL to access the file
     */
    String getFileUrl(String key);
    
    /**
     * Delete a file from storage
     * @param key The storage key/path of the file to delete
     */
    void deleteFile(String key);
    
    /**
     * Check if a file exists in storage
     * @param key The storage key/path of the file
     * @return true if file exists, false otherwise
     */
    boolean doesFileExist(String key);
}
