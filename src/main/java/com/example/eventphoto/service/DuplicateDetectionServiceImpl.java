package com.example.eventphoto.service;

import com.example.eventphoto.model.Image;
import com.example.eventphoto.repository.ImageRepository;
import dev.brachtendorf.jimagehash.hashAlgorithms.PerceptiveHash;
import dev.brachtendorf.jimagehash.hashAlgorithms.hashingAlgorithms.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DuplicateDetectionServiceImpl implements DuplicateDetectionService {

    private static final double DUPLICATE_THRESHOLD = 0.2;

    private final ImageRepository imageRepository;
    private final PerceptiveHash hasher = new PerceptiveHash(32);

    @Override
    public String computeHash(MultipartFile file) throws Exception {
        BufferedImage img = ImageIO.read(file.getInputStream());
        if (img == null) return null;
        Hash hash = hasher.hash(img);
        byte[] bytes = hash.getHashValue();
        return Base64.getEncoder().encodeToString(bytes);
    }

    @Override
    public Optional<Long> findDuplicate(Long eventId, Long guestId, String perceptualHash) {
        if (perceptualHash == null) return Optional.empty();
        List<Image> existing = imageRepository.findByEventIdAndGuestId(eventId, guestId);
        byte[] newBytes;
        try {
            newBytes = Base64.getDecoder().decode(perceptualHash);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
        Hash newHash = new Hash(newBytes);
        for (Image img : existing) {
            if (img.getPerceptualHash() == null) continue;
            try {
                byte[] existingBytes = Base64.getDecoder().decode(img.getPerceptualHash());
                Hash existingHash = new Hash(existingBytes);
                if (newHash.normalizedHammingDistance(existingHash) < DUPLICATE_THRESHOLD) {
                    return Optional.of(img.getId());
                }
            } catch (Exception ignored) {
            }
        }
        return Optional.empty();
    }
}
