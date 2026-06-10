package com.mithila.royalpaan.dto;

import java.time.LocalDateTime;

public class ChatConversationDTO {

    private Long id;
    private String sessionId;
    private String visitorName;
    private String visitorEmail;
    private String visitorPhone;
    private String status;
    private Integer messageCount;
    private Boolean leadGenerated;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ChatConversationDTO() {}

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

    public Integer getMessageCount() { return messageCount; }
    public void setMessageCount(Integer messageCount) { this.messageCount = messageCount; }

    public Boolean getLeadGenerated() { return leadGenerated; }
    public void setLeadGenerated(Boolean leadGenerated) { this.leadGenerated = leadGenerated; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
