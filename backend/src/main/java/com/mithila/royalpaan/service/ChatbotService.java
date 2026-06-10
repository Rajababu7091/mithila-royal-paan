package com.mithila.royalpaan.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mithila.royalpaan.dto.ChatConversationDTO;
import com.mithila.royalpaan.dto.ChatLeadDTO;
import com.mithila.royalpaan.dto.ChatRequest;
import com.mithila.royalpaan.dto.ChatResponse;
import com.mithila.royalpaan.entity.ChatbotConversation;
import com.mithila.royalpaan.entity.ChatbotLead;
import com.mithila.royalpaan.entity.ChatbotMessage;
import com.mithila.royalpaan.repository.ChatbotConversationRepository;
import com.mithila.royalpaan.repository.ChatbotLeadRepository;
import com.mithila.royalpaan.repository.ChatbotMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatbotService {

    @Autowired
    private ChatbotConversationRepository conversationRepo;

    @Autowired
    private ChatbotMessageRepository messageRepo;

    @Autowired
    private ChatbotLeadRepository leadRepo;

    @Autowired
    private OpenAiService openAiService;

    /**
     * Handle an incoming chat message:
     * 1. Find or create conversation by sessionId
     * 2. Load message history
     * 3. Call OpenAI
     * 4. Save user and assistant messages
     * 5. Detect and save lead if AI signals one
     * 6. Return cleaned reply to frontend
     */
    public ChatResponse processMessage(ChatRequest request) {
        // 1. Get or create conversation
        ChatbotConversation conversation = conversationRepo.findBySessionId(request.getSessionId())
            .orElseGet(() -> {
                ChatbotConversation c = new ChatbotConversation(request.getSessionId());
                if (request.getVisitorName() != null && !request.getVisitorName().isBlank()) {
                    c.setVisitorName(request.getVisitorName());
                }
                return conversationRepo.save(c);
            });

        // Update visitor name if newly provided
        if (request.getVisitorName() != null && !request.getVisitorName().isBlank()
                && (conversation.getVisitorName() == null || conversation.getVisitorName().isBlank())) {
            conversation.setVisitorName(request.getVisitorName());
        }

        // 2. Load conversation history for context
        List<ChatbotMessage> history = messageRepo.findByConversationOrderByCreatedAtAsc(conversation);
        List<Map<String, String>> historyForAI = history.stream()
            .map(m -> Map.of("role", m.getRole(), "content", m.getContent()))
            .collect(Collectors.toList());

        // 3. Call OpenAI
        String rawReply = openAiService.chat(historyForAI, request.getMessage());

        // 4. Save messages
        ChatbotMessage userMsg = new ChatbotMessage(conversation, "USER", request.getMessage());
        messageRepo.save(userMsg);

        String cleanReply = openAiService.cleanReply(rawReply);
        ChatbotMessage assistantMsg = new ChatbotMessage(conversation, "ASSISTANT", cleanReply);
        messageRepo.save(assistantMsg);

        // Update message count
        conversation.setMessageCount((conversation.getMessageCount() == null ? 0 : conversation.getMessageCount()) + 2);

        // 5. Check for lead data
        boolean leadCaptured = false;
        JsonNode leadData = openAiService.extractLeadData(rawReply);
        if (leadData != null && !conversation.getLeadGenerated()) {
            ChatbotLead lead = new ChatbotLead();
            lead.setConversation(conversation);
            lead.setFullName(getJsonField(leadData, "name"));
            lead.setMobile(getJsonField(leadData, "mobile"));
            lead.setWhatsapp(getJsonField(leadData, "whatsapp"));
            lead.setEmail(getJsonField(leadData, "email"));
            lead.setCity(getJsonField(leadData, "city"));
            lead.setRequirement(getJsonField(leadData, "requirement"));
            leadRepo.save(lead);

            // Update conversation with visitor details
            if (lead.getFullName() != null && conversation.getVisitorName() == null) {
                conversation.setVisitorName(lead.getFullName());
            }
            if (lead.getMobile() != null) {
                conversation.setVisitorPhone(lead.getMobile());
            }
            if (lead.getEmail() != null) {
                conversation.setVisitorEmail(lead.getEmail());
            }
            conversation.setLeadGenerated(true);
            leadCaptured = true;
        }

        conversationRepo.save(conversation);

        return new ChatResponse(request.getSessionId(), cleanReply, leadCaptured);
    }

    // ==================== ADMIN METHODS ====================

    public List<ChatConversationDTO> getAllConversations() {
        return conversationRepo.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(this::toConversationDTO)
            .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getConversationMessages(Long conversationId) {
        ChatbotConversation conv = conversationRepo.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation not found"));
        List<ChatbotMessage> messages = messageRepo.findByConversationOrderByCreatedAtAsc(conv);
        return messages.stream()
            .map(m -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", m.getId());
                map.put("role", m.getRole());
                map.put("content", m.getContent());
                map.put("createdAt", m.getCreatedAt());
                return map;
            })
            .collect(Collectors.toList());
    }

    public List<ChatLeadDTO> getAllLeads() {
        return leadRepo.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(this::toLeadDTO)
            .collect(Collectors.toList());
    }

    public ChatLeadDTO updateLeadStatus(Long leadId, String status) {
        ChatbotLead lead = leadRepo.findById(leadId)
            .orElseThrow(() -> new RuntimeException("Lead not found"));
        lead.setStatus(status);
        return toLeadDTO(leadRepo.save(lead));
    }

    public ChatLeadDTO assignLead(Long leadId, String assignedTo) {
        ChatbotLead lead = leadRepo.findById(leadId)
            .orElseThrow(() -> new RuntimeException("Lead not found"));
        lead.setAssignedTo(assignedTo);
        return toLeadDTO(leadRepo.save(lead));
    }

    public void deleteConversation(Long conversationId) {
        ChatbotConversation conv = conversationRepo.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation not found"));
        messageRepo.findByConversationOrderByCreatedAtAsc(conv)
            .forEach(messageRepo::delete);
        conversationRepo.delete(conv);
    }

    public Map<String, Object> getAnalytics() {
        Map<String, Object> analytics = new LinkedHashMap<>();
        long totalConversations = conversationRepo.count();
        long totalLeads = leadRepo.count();
        long newLeads = leadRepo.countByStatus("NEW");
        long contactedLeads = leadRepo.countByStatus("CONTACTED");
        long convertedLeads = leadRepo.countByStatus("CONVERTED");
        long convWithLeads = conversationRepo.countByLeadGeneratedTrue();

        analytics.put("totalConversations", totalConversations);
        analytics.put("totalLeads", totalLeads);
        analytics.put("newLeads", newLeads);
        analytics.put("contactedLeads", contactedLeads);
        analytics.put("convertedLeads", convertedLeads);
        analytics.put("conversionRate", totalConversations > 0
            ? String.format("%.1f%%", (convWithLeads * 100.0) / totalConversations)
            : "0%");
        return analytics;
    }

    // ==================== HELPER METHODS ====================

    private String getJsonField(JsonNode node, String field) {
        if (node == null || !node.has(field)) return null;
        String val = node.get(field).asText().trim();
        return val.isEmpty() ? null : val;
    }

    private ChatConversationDTO toConversationDTO(ChatbotConversation c) {
        ChatConversationDTO dto = new ChatConversationDTO();
        dto.setId(c.getId());
        dto.setSessionId(c.getSessionId());
        dto.setVisitorName(c.getVisitorName());
        dto.setVisitorEmail(c.getVisitorEmail());
        dto.setVisitorPhone(c.getVisitorPhone());
        dto.setStatus(c.getStatus());
        dto.setMessageCount(c.getMessageCount());
        dto.setLeadGenerated(c.getLeadGenerated());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setUpdatedAt(c.getUpdatedAt());
        return dto;
    }

    private ChatLeadDTO toLeadDTO(ChatbotLead l) {
        ChatLeadDTO dto = new ChatLeadDTO();
        dto.setId(l.getId());
        dto.setSessionId(l.getConversation() != null ? l.getConversation().getSessionId() : null);
        dto.setFullName(l.getFullName());
        dto.setMobile(l.getMobile());
        dto.setWhatsapp(l.getWhatsapp());
        dto.setEmail(l.getEmail());
        dto.setCity(l.getCity());
        dto.setRequirement(l.getRequirement());
        dto.setStatus(l.getStatus());
        dto.setAssignedTo(l.getAssignedTo());
        dto.setNotes(l.getNotes());
        dto.setCreatedAt(l.getCreatedAt());
        return dto;
    }
}
