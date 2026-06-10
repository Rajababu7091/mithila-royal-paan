package com.mithila.royalpaan.controller;

import com.mithila.royalpaan.dto.MessageResponse;
import com.mithila.royalpaan.entity.Testimonial;
import com.mithila.royalpaan.service.TestimonialService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/testimonials")
public class TestimonialController {

    @Autowired
    private TestimonialService testimonialService;

    @GetMapping
    public ResponseEntity<List<Testimonial>> getTestimonials() {
        return ResponseEntity.ok(testimonialService.getAllTestimonials());
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> createTestimonial(@Valid @RequestBody Testimonial testimonial) {
        try {
            Testimonial saved = testimonialService.saveTestimonial(testimonial);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteTestimonial(@PathVariable Integer id) {
        try {
            testimonialService.deleteTestimonial(id);
            return ResponseEntity.ok(new MessageResponse("Testimonial deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
