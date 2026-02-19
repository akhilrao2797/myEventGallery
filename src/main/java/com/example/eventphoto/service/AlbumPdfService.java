package com.example.eventphoto.service;

import com.example.eventphoto.model.Image;
import com.example.eventphoto.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlbumPdfService {

    private final ImageRepository imageRepository;
    private final StorageService storageService;

    private static final float PAGE_WIDTH = 595;
    private static final float PAGE_HEIGHT = 842;
    private static final float MARGIN = 40;
    private static final float MAX_IMAGE_WIDTH = PAGE_WIDTH - 2 * MARGIN;
    private static final float MAX_IMAGE_HEIGHT = PAGE_HEIGHT - 2 * MARGIN;

    /**
     * Generate a PDF album from the given image IDs. Caller must ensure customer owns the event.
     */
    public byte[] generatePdf(List<Long> imageIds) throws IOException {
        List<Image> images = imageRepository.findByIdIn(imageIds);
        if (images.isEmpty()) {
            throw new RuntimeException("No images selected");
        }
        try (PDDocument document = new PDDocument()) {
            for (Image img : images) {
                byte[] bytes = storageService.getFileBytes(img.getStorageKey());
                if (bytes == null || bytes.length == 0) continue;
                PDPage page = new PDPage(new PDRectangle(PAGE_WIDTH, PAGE_HEIGHT));
                document.addPage(page);
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    PDImageXObject pdImage;
                    String contentType = img.getContentType();
                    if (contentType != null && contentType.toLowerCase().contains("png")) {
                        pdImage = org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory.createFromByteArray(document, bytes, img.getFileName());
                    } else {
                        pdImage = JPEGFactory.createFromByteArray(document, bytes);
                    }
                    float imgWidth = pdImage.getWidth();
                    float imgHeight = pdImage.getHeight();
                    float scale = Math.min(MAX_IMAGE_WIDTH / imgWidth, MAX_IMAGE_HEIGHT / imgHeight);
                    float drawWidth = imgWidth * scale;
                    float drawHeight = imgHeight * scale;
                    float x = MARGIN + (MAX_IMAGE_WIDTH - drawWidth) / 2;
                    float y = PAGE_HEIGHT - MARGIN - drawHeight;
                    contentStream.drawImage(pdImage, x, y, drawWidth, drawHeight);
                }
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }
}
