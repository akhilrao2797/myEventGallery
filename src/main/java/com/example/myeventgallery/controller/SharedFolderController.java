package com.example.myeventgallery.controller;

import com.example.myeventgallery.dto.ApiResponse;
import com.example.myeventgallery.dto.CreateSharedFolderRequest;
import com.example.myeventgallery.dto.SharedFolderResponse;
import com.example.myeventgallery.security.JwtUtil;
import com.example.myeventgallery.service.SharedFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shared-folders")
public class SharedFolderController {
    
    @Autowired
    private SharedFolderService sharedFolderService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping
    public ResponseEntity<ApiResponse<SharedFolderResponse>> createSharedFolder(
            @RequestBody CreateSharedFolderRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long customerId = jwtUtil.extractCustomerId(token);
            
            SharedFolderResponse response = sharedFolderService.createSharedFolder(request, customerId);
            return ResponseEntity.ok(ApiResponse.success("Shared folder created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<SharedFolderResponse>>> getMySharedFolders(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long customerId = jwtUtil.extractCustomerId(token);
            
            List<SharedFolderResponse> folders = sharedFolderService.getCustomerFolders(customerId);
            return ResponseEntity.ok(ApiResponse.success("Folders retrieved successfully", folders));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/public/{shareCode}")
    public ResponseEntity<ApiResponse<SharedFolderResponse>> getSharedFolder(
            @PathVariable String shareCode,
            @RequestParam(required = false) String password) {
        try {
            SharedFolderResponse folder = sharedFolderService.getSharedFolder(shareCode, password);
            return ResponseEntity.ok(ApiResponse.success("Folder retrieved successfully", folder));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{folderId}/images")
    public ResponseEntity<ApiResponse<Void>> updateFolderImages(
            @PathVariable Long folderId,
            @RequestBody Map<String, List<Long>> request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long customerId = jwtUtil.extractCustomerId(token);
            
            List<Long> imageIds = request.get("imageIds");
            sharedFolderService.updateFolderImages(folderId, imageIds, customerId);
            return ResponseEntity.ok(ApiResponse.success("Folder images updated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{folderId}")
    public ResponseEntity<ApiResponse<Void>> deleteSharedFolder(
            @PathVariable Long folderId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long customerId = jwtUtil.extractCustomerId(token);
            
            sharedFolderService.deleteSharedFolder(folderId, customerId);
            return ResponseEntity.ok(ApiResponse.success("Folder deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/{folderId}/download-count")
    public ResponseEntity<ApiResponse<Void>> incrementDownloadCount(@PathVariable Long folderId) {
        try {
            sharedFolderService.incrementDownloadCount(folderId);
            return ResponseEntity.ok(ApiResponse.success("Download count updated", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
