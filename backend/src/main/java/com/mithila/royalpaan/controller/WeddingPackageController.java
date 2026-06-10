package com.mithila.royalpaan.controller;

import com.mithila.royalpaan.entity.WeddingPackage;
import com.mithila.royalpaan.repository.WeddingPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/wedding-packages")
public class WeddingPackageController {

    @Autowired
    private WeddingPackageRepository weddingPackageRepository;

    @GetMapping
    public ResponseEntity<List<WeddingPackage>> getAllPackages() {
        return ResponseEntity.ok(weddingPackageRepository.findAllByOrderByDisplayOrderAsc());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STAFF')")
    public ResponseEntity<WeddingPackage> createPackage(@RequestBody WeddingPackage pkg) {
        return ResponseEntity.ok(weddingPackageRepository.save(pkg));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STAFF')")
    public ResponseEntity<WeddingPackage> updatePackage(@PathVariable Integer id, @RequestBody WeddingPackage pkgDetails) {
        WeddingPackage pkg = weddingPackageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wedding Package not found with id: " + id));
        pkg.setName(pkgDetails.getName());
        pkg.setDescription(pkgDetails.getDescription());
        pkg.setPrice(pkgDetails.getPrice());
        pkg.setFeatures(pkgDetails.getFeatures());
        pkg.setDisplayOrder(pkgDetails.getDisplayOrder());
        return ResponseEntity.ok(weddingPackageRepository.save(pkg));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STAFF')")
    public ResponseEntity<?> deletePackage(@PathVariable Integer id) {
        WeddingPackage pkg = weddingPackageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wedding Package not found with id: " + id));
        weddingPackageRepository.delete(pkg);
        return ResponseEntity.ok(Map.of("message", "Wedding Package deleted successfully"));
    }
}
