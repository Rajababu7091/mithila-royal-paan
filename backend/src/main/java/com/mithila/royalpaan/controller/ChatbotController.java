package com.mithila.royalpaan.controller;

import com.mithila.royalpaan.dto.ChatConversationDTO;
import com.mithila.royalpaan.dto.ChatLeadDTO;
import com.mithila.royalpaan.dto.ChatRequest;
import com.mithila.royalpaan.dto.ChatResponse;
import com.mithila.royalpaan.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    /**
     * PUBLIC endpoint — any visitor can send a chat message.
     * Session is identified by sessionId (generated in browser, stored in localStorage).
     */
    @PostMapping("/message")
    public ResponseEntity<ChatResponse> sendMessage(@RequestBody ChatRequest request) {
        if (request.getSessionId() == null || request.getSessionId().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (request.getMessage() == null || request.getMessage().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        ChatResponse response = chatbotService.processMessage(request);
        return ResponseEntity.ok(response);
    }

    /**
     * ADMIN — Get all conversations
     */
    @GetMapping("/admin/conversations")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<ChatConversationDTO>> getAllConversations() {
        return ResponseEntity.ok(chatbotService.getAllConversations());
    }

    /**
     * ADMIN — Get all messages in a conversation
     */
    @GetMapping("/admin/conversations/{id}/messages")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<Map<String, Object>>> getConversationMessages(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(chatbotService.getConversationMessages(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * ADMIN — Delete a conversation and its messages
     */
    @DeleteMapping("/admin/conversations/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteConversation(@PathVariable Long id) {
        try {
            chatbotService.deleteConversation(id);
            return ResponseEntity.ok(Map.of("message", "Conversation deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * ADMIN — Get all leads captured by AI
     */
    @GetMapping("/admin/leads")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<ChatLeadDTO>> getAllLeads() {
        return ResponseEntity.ok(chatbotService.getAllLeads());
    }

    /**
     * ADMIN — Update lead status
     */
    @PutMapping("/admin/leads/{id}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> updateLeadStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            ChatLeadDTO updated = chatbotService.updateLeadStatus(id, body.get("status"));
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * ADMIN — Assign lead to staff
     */
    @PutMapping("/admin/leads/{id}/assign")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> assignLead(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            ChatLeadDTO updated = chatbotService.assignLead(id, body.get("assignedTo"));
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * ADMIN — Get analytics summary
     */
    @GetMapping("/admin/analytics")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        return ResponseEntity.ok(chatbotService.getAnalytics());
    }
}
