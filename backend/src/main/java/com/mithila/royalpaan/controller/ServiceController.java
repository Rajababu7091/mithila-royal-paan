package com.mithila.royalpaan.controller;

import com.mithila.royalpaan.entity.Service;
import com.mithila.royalpaan.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceController {

    @Autowired
    private ServiceRepository serviceRepository;

    @GetMapping
    public ResponseEntity<List<Service>> getAllServices() {
        return ResponseEntity.ok(serviceRepository.findAllByOrderByDisplayOrderAsc());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STAFF')")
    public ResponseEntity<Service> createService(@RequestBody Service service) {
        return ResponseEntity.ok(serviceRepository.save(service));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STAFF')")
    public ResponseEntity<Service> updateService(@PathVariable Integer id, @RequestBody Service serviceDetails) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));
        service.setName(serviceDetails.getName());
        service.setDescription(serviceDetails.getDescription());
        service.setImageUrl(serviceDetails.getImageUrl());
        service.setPrice(serviceDetails.getPrice());
        service.setFeatures(serviceDetails.getFeatures());
        service.setDisplayOrder(serviceDetails.getDisplayOrder());
        return ResponseEntity.ok(serviceRepository.save(service));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STAFF')")
    public ResponseEntity<?> deleteService(@PathVariable Integer id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));
        serviceRepository.delete(service);
        return ResponseEntity.ok(Map.of("message", "Service deleted successfully"));
    }
}
