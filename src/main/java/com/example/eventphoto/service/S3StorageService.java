package com.example.eventphoto.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@Profile("prod")
public class S3StorageService implements StorageService {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region:ap-south-1}")
    private String region;

    private final S3Client s3Client;

    public S3StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String uploadFile(MultipartFile file, String eventCode) throws IOException {
        String ext = getExtension(file.getOriginalFilename());
        String key = "events/" + eventCode + "/" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();
        s3Client.putObject(req, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return key;
    }

    @Override
    public void upload(String storageKey, InputStream content, String contentType) throws IOException {
        byte[] bytes = content.readAllBytes();
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(storageKey)
                .contentType(contentType != null ? contentType : "application/octet-stream")
                .build();
        s3Client.putObject(req, RequestBody.fromBytes(bytes));
    }

    @Override
    public void delete(String storageKey) {
        deleteFile(storageKey);
    }

    @Override
    public void deleteFile(String storageKey) {
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(storageKey).build());
    }

    @Override
    public String getPublicUrl(String storageKey) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, storageKey);
    }

    @Override
    public InputStream getFileStream(String storageKey) throws IOException {
        return s3Client.getObject(GetObjectRequest.builder().bucket(bucketName).key(storageKey).build());
    }

    @Override
    public byte[] getFileBytes(String storageKey) throws IOException {
        try (InputStream is = getFileStream(storageKey)) {
            return is.readAllBytes();
        }
    }

    private static String getExtension(String name) {
        if (name == null || !name.contains(".")) return ".jpg";
        return name.substring(name.lastIndexOf('.'));
    }
}
