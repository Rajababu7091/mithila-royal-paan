package com.mithila.royalpaan.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "export_enquiries")
public class ExportEnquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(length = 100)
    private String company;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(name = "quantity_requirement", nullable = false, length = 100)
    private String quantityRequirement;

    @Column(name = "contact_info", nullable = false, length = 255)
    private String contactInfo;

    @Column(name = "business_type", nullable = false, length = 100)
    private String businessType; // Importer, Distributor, Retailer, Event Planner, Other

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(length = 20)
    private String status = "PENDING"; // PENDING, CONTACTED, CLOSED

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public ExportEnquiry() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getQuantityRequirement() {
        return quantityRequirement;
    }

    public void setQuantityRequirement(String quantityRequirement) {
        this.quantityRequirement = quantityRequirement;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
