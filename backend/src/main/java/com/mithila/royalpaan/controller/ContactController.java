package com.mithila.royalpaan.controller;

import com.mithila.royalpaan.dto.ContactRequest;
import com.mithila.royalpaan.dto.MessageResponse;
import com.mithila.royalpaan.entity.ContactMessage;
import com.mithila.royalpaan.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contact")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping
    public ResponseEntity<?> submitContactMessage(@Valid @RequestBody ContactRequest contactRequest) {
        try {
            ContactMessage saved = contactService.saveMessage(contactRequest);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<ContactMessage>> getAllContactMessages() {
        return ResponseEntity.ok(contactService.getAllMessages());
    }

    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> updateMessageStatus(@PathVariable Integer id, @RequestParam String status) {
        try {
            ContactMessage updated = contactService.updateStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
