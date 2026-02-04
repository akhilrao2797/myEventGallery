package com.example.myeventgallery.controller;

import com.example.myeventgallery.service.LocalStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Controller to serve files from local storage in development mode
 */
@RestController
@RequestMapping("/api/files")
@Profile({"local", "dev", "default"})
public class FileController {
    
    @Autowired
    private LocalStorageService localStorageService;
    
    @GetMapping("/{eventCode}/**")
    public ResponseEntity<Resource> serveFile(@PathVariable String eventCode, HttpServletRequest request) {
        try {
            // Extract the full path after /api/files/
            String requestUri = request.getRequestURI();
            String fullPath = requestUri.substring(requestUri.indexOf("/api/files/") + 11);
            
            // Clean the path - remove leading slash if present
            fullPath = fullPath.startsWith("/") ? fullPath.substring(1) : fullPath;
            
            // Get file from local storage
            byte[] fileBytes = localStorageService.getFile(fullPath);
            
            // Determine content type
            String contentType = determineContentType(fullPath);
            
            ByteArrayResource resource = new ByteArrayResource(fileBytes);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=31536000")
                    .body(resource);
                    
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    private String determineContentType(String filename) {
        if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.toLowerCase().endsWith(".png")) {
            return "image/png";
        } else if (filename.toLowerCase().endsWith(".gif")) {
            return "image/gif";
        } else if (filename.toLowerCase().endsWith(".webp")) {
            return "image/webp";
        }
        return "application/octet-stream";
    }
}
