package com.mithila.royalpaan.controller;

import com.mithila.royalpaan.dto.ExportEnquiryRequest;
import com.mithila.royalpaan.dto.MessageResponse;
import com.mithila.royalpaan.entity.ExportEnquiry;
import com.mithila.royalpaan.service.ExportEnquiryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/export-enquiries")
public class ExportEnquiryController {

    @Autowired
    private ExportEnquiryService exportEnquiryService;

    @PostMapping
    public ResponseEntity<?> submitEnquiry(@Valid @RequestBody ExportEnquiryRequest enquiryRequest) {
        try {
            ExportEnquiry enquiry = exportEnquiryService.submitEnquiry(enquiryRequest);
            return ResponseEntity.ok(enquiry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<ExportEnquiry>> getAllEnquiries() {
        List<ExportEnquiry> enquiries = exportEnquiryService.getAllEnquiries();
        return ResponseEntity.ok(enquiries);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> updateEnquiryStatus(@PathVariable Integer id, @RequestParam String status) {
        try {
            ExportEnquiry enquiry = exportEnquiryService.updateStatus(id, status);
            return ResponseEntity.ok(enquiry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
