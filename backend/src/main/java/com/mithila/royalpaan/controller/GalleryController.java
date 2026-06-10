package com.mithila.royalpaan.controller;

import com.mithila.royalpaan.dto.MessageResponse;
import com.mithila.royalpaan.entity.Gallery;
import com.mithila.royalpaan.service.GalleryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/gallery")
public class GalleryController {

    @Autowired
    private GalleryService galleryService;

    @GetMapping
    public ResponseEntity<List<Gallery>> getGalleryItems() {
        return ResponseEntity.ok(galleryService.getAllGalleryItems());
    }

    @GetMapping("/category")
    public ResponseEntity<List<Gallery>> getGalleryByCategory(@RequestParam String category) {
        return ResponseEntity.ok(galleryService.getGalleryItemsByCategory(category));
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> createGalleryItem(@Valid @RequestBody Gallery item) {
        try {
            Gallery saved = galleryService.saveGalleryItem(item);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteGalleryItem(@PathVariable Integer id) {
        try {
            galleryService.deleteGalleryItem(id);
            return ResponseEntity.ok(new MessageResponse("Gallery item deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
