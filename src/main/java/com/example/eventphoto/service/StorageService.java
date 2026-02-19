package com.example.eventphoto.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface StorageService {
    String uploadFile(MultipartFile file, String eventCode) throws IOException;
    void upload(String storageKey, InputStream content, String contentType) throws IOException;
    void delete(String storageKey) throws IOException;
    void deleteFile(String storageKey) throws IOException;
    String getPublicUrl(String storageKey);
    InputStream getFileStream(String storageKey) throws IOException;
    byte[] getFileBytes(String storageKey) throws IOException;
}
