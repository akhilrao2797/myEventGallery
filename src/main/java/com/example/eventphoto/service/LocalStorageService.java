package com.example.eventphoto.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Profile("!prod")
public class LocalStorageService implements StorageService {

    @Value("${storage.local.base-path:./uploads}")
    private String basePath;

    @Override
    public String uploadFile(MultipartFile file, String eventCode) throws IOException {
        Path dir = Paths.get(basePath, "events", eventCode);
        Files.createDirectories(dir);
        String ext = getExtension(file.getOriginalFilename());
        String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;
        Path target = dir.resolve(fileName);
        Files.copy(file.getInputStream(), target);
        return "events/" + eventCode + "/" + fileName;
    }

    @Override
    public void upload(String storageKey, InputStream content, String contentType) throws IOException {
        Path path = Paths.get(basePath, storageKey);
        Files.createDirectories(path.getParent());
        Files.copy(content, path);
    }

    @Override
    public void delete(String storageKey) throws IOException {
        deleteFile(storageKey);
    }

    @Override
    public void deleteFile(String storageKey) throws IOException {
        Path path = Paths.get(basePath, storageKey);
        if (Files.exists(path)) Files.delete(path);
    }

    @Override
    public String getPublicUrl(String storageKey) {
        return "/api/files?key=" + java.net.URLEncoder.encode(storageKey, java.nio.charset.StandardCharsets.UTF_8);
    }

    @Override
    public InputStream getFileStream(String storageKey) throws IOException {
        return Files.newInputStream(Paths.get(basePath, storageKey));
    }

    @Override
    public byte[] getFileBytes(String storageKey) throws IOException {
        return Files.readAllBytes(Paths.get(basePath, storageKey));
    }

    private static String getExtension(String name) {
        if (name == null || !name.contains(".")) return ".jpg";
        return name.substring(name.lastIndexOf('.'));
    }
}
