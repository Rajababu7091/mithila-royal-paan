package com.mithila.royalpaan.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chatbot_conversations")
public class ChatbotConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false, unique = true, length = 100)
    private String sessionId;

    @Column(name = "visitor_name", length = 100)
    private String visitorName;

    @Column(name = "visitor_email", length = 150)
    private String visitorEmail;

    @Column(name = "visitor_phone", length = 20)
    private String visitorPhone;

    @Column(name = "status", length = 20)
    private String status = "ACTIVE"; // ACTIVE, CLOSED

    @Column(name = "channel", length = 30)
    private String channel = "WEB";

    @Column(name = "message_count")
    private Integer messageCount = 0;

    @Column(name = "lead_generated")
    private Boolean leadGenerated = false;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ChatbotConversation() {}

    public ChatbotConversation(String sessionId) {
        this.sessionId = sessionId;
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // --- Getters & Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getVisitorName() { return visitorName; }
    public void setVisitorName(String visitorName) { this.visitorName = visitorName; }

    public String getVisitorEmail() { return visitorEmail; }
    public void setVisitorEmail(String visitorEmail) { this.visitorEmail = visitorEmail; }

    public String getVisitorPhone() { return visitorPhone; }
    public void setVisitorPhone(String visitorPhone) { this.visitorPhone = visitorPhone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public Integer getMessageCount() { return messageCount; }
    public void setMessageCount(Integer messageCount) { this.messageCount = messageCount; }

    public Boolean getLeadGenerated() { return leadGenerated; }
    public void setLeadGenerated(Boolean leadGenerated) { this.leadGenerated = leadGenerated; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
