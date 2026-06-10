package com.mithila.royalpaan.controller;

import com.mithila.royalpaan.entity.Faq;
import com.mithila.royalpaan.repository.FaqRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/faqs")
public class FaqController {

    @Autowired
    private FaqRepository faqRepository;

    @GetMapping
    public ResponseEntity<List<Faq>> getAllFaqs() {
        return ResponseEntity.ok(faqRepository.findAllByOrderByDisplayOrderAsc());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STAFF')")
    public ResponseEntity<Faq> createFaq(@RequestBody Faq faq) {
        return ResponseEntity.ok(faqRepository.save(faq));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STAFF')")
    public ResponseEntity<Faq> updateFaq(@PathVariable Integer id, @RequestBody Faq faqDetails) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ not found with id: " + id));
        faq.setQuestion(faqDetails.getQuestion());
        faq.setAnswer(faqDetails.getAnswer());
        faq.setDisplayOrder(faqDetails.getDisplayOrder());
        return ResponseEntity.ok(faqRepository.save(faq));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STAFF')")
    public ResponseEntity<?> deleteFaq(@PathVariable Integer id) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ not found with id: " + id));
        faqRepository.delete(faq);
        return ResponseEntity.ok(Map.of("message", "FAQ deleted successfully"));
    }
}
