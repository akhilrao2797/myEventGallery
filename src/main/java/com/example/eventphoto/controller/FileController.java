package com.example.eventphoto.controller;

import com.example.eventphoto.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final StorageService storageService;

    @GetMapping("/**")
    public ResponseEntity<byte[]> serve(@RequestParam("key") String key) throws IOException {
        byte[] bytes = storageService.getFileBytes(key);
        if (bytes == null) {
            return ResponseEntity.notFound().build();
        }
        String contentType = "application/octet-stream";
        if (key.toLowerCase().endsWith(".jpg") || key.toLowerCase().endsWith(".jpeg")) {
            contentType = "image/jpeg";
        } else if (key.toLowerCase().endsWith(".png")) {
            contentType = "image/png";
        } else if (key.toLowerCase().endsWith(".gif")) {
            contentType = "image/gif";
        } else if (key.toLowerCase().endsWith(".webp")) {
            contentType = "image/webp";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(bytes);
    }
}
