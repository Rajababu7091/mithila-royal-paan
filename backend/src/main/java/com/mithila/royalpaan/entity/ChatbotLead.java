package com.mithila.royalpaan.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chatbot_leads")
public class ChatbotLead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private ChatbotConversation conversation;

    @Column(name = "full_name", length = 150)
    private String fullName;

    @Column(name = "mobile", length = 20)
    private String mobile;

    @Column(name = "whatsapp", length = 20)
    private String whatsapp;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "requirement", columnDefinition = "TEXT")
    private String requirement;

    @Column(name = "status", length = 20)
    private String status = "NEW"; // NEW, CONTACTED, CONVERTED, LOST

    @Column(name = "assigned_to", length = 100)
    private String assignedTo;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public ChatbotLead() {}

    // --- Getters & Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ChatbotConversation getConversation() { return conversation; }
    public void setConversation(ChatbotConversation conversation) { this.conversation = conversation; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getWhatsapp() { return whatsapp; }
    public void setWhatsapp(String whatsapp) { this.whatsapp = whatsapp; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getRequirement() { return requirement; }
    public void setRequirement(String requirement) { this.requirement = requirement; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
