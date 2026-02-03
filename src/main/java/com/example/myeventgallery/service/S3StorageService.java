package com.example.myeventgallery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

/**
 * AWS S3 storage implementation for production
 */
@Service
@Profile({"prod", "production"})
public class S3StorageService implements StorageService {
    
    @Autowired
    private S3Client s3Client;
    
    @Autowired
    private S3Presigner s3Presigner;
    
    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    
    @Override
    public String uploadFile(MultipartFile file, String folderPath) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String key = folderPath + fileName;
        
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();
        
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
        
        return key;
    }
    
    @Override
    public String getFileUrl(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(24))
                .getObjectRequest(getObjectRequest)
                .build();
        
        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        
        return presignedRequest.url().toString();
    }
    
    @Override
    public void deleteFile(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        
        s3Client.deleteObject(deleteObjectRequest);
    }
    
    @Override
    public boolean doesFileExist(String key) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            
            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }
}
