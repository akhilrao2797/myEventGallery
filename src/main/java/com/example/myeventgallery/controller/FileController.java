package com.example.myeventgallery.controller;

import com.example.myeventgallery.service.LocalStorageService;
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
    public ResponseEntity<Resource> serveFile(@PathVariable String eventCode, @RequestParam String file) {
        try {
            // Construct full path: events/{eventCode}/{filename}
            String fullPath = "events/" + eventCode + "/" + file;
            
            // Get file from local storage
            byte[] fileBytes = localStorageService.getFile(fullPath);
            
            // Determine content type
            String contentType = determineContentType(file);
            
            ByteArrayResource resource = new ByteArrayResource(fileBytes);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
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
