package com.mithila.royalpaan.controller;

import com.mithila.royalpaan.entity.MediaFile;
import com.mithila.royalpaan.repository.MediaFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/media")
public class MediaController {

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STAFF')")
    public ResponseEntity<List<MediaFile>> getAllMedia() {
        return ResponseEntity.ok(mediaFileRepository.findAll());
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STAFF')")
    public ResponseEntity<?> uploadMedia(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Uploaded file is empty"));
        }

        try {
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            Path filePath = Paths.get(uploadDir, uniqueFilename);
            Files.copy(file.getInputStream(), filePath);

            String fileUrl = "/uploads/" + uniqueFilename;
            String fileType = "OTHER";
            String contentType = file.getContentType();
            if (contentType != null) {
                if (contentType.startsWith("image")) {
                    fileType = "IMAGE";
                } else if (contentType.startsWith("video")) {
                    fileType = "VIDEO";
                }
            }

            MediaFile mediaFile = new MediaFile(originalFilename, fileUrl, fileType, file.getSize());
            MediaFile savedMedia = mediaFileRepository.save(mediaFile);

            return ResponseEntity.ok(savedMedia);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to store file: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STAFF')")
    public ResponseEntity<?> deleteMedia(@PathVariable Integer id) {
        MediaFile mediaFile = mediaFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Media file not found with id: " + id));

        try {
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
            String filename = mediaFile.getFileUrl().substring(mediaFile.getFileUrl().lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadDir, filename);
            Files.deleteIfExists(filePath);

            mediaFileRepository.delete(mediaFile);
            return ResponseEntity.ok(Map.of("message", "Media file deleted successfully"));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to delete physical file: " + e.getMessage()));
        }
    }
}
