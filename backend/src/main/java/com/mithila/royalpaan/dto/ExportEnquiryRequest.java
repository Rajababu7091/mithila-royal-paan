package com.mithila.royalpaan.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ExportEnquiryRequest {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    private String company;

    @NotBlank
    private String country;

    @NotBlank
    private String quantityRequirement;

    @NotBlank
    private String contactInfo;

    @NotBlank
    private String businessType;

    private String message;

    public ExportEnquiryRequest() {}

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
}
