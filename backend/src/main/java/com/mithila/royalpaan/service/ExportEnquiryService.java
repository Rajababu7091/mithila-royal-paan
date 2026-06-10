package com.mithila.royalpaan.service;

import com.mithila.royalpaan.entity.ExportEnquiry;
import com.mithila.royalpaan.repository.ExportEnquiryRepository;
import com.mithila.royalpaan.dto.ExportEnquiryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExportEnquiryService {

    @Autowired
    private ExportEnquiryRepository exportEnquiryRepository;

    public ExportEnquiry submitEnquiry(ExportEnquiryRequest req) {
        ExportEnquiry enquiry = new ExportEnquiry();
        enquiry.setName(req.getName());
        enquiry.setEmail(req.getEmail());
        enquiry.setCompany(req.getCompany());
        enquiry.setCountry(req.getCountry());
        enquiry.setQuantityRequirement(req.getQuantityRequirement());
        enquiry.setContactInfo(req.getContactInfo());
        enquiry.setBusinessType(req.getBusinessType());
        enquiry.setMessage(req.getMessage());
        enquiry.setStatus("PENDING");

        return exportEnquiryRepository.save(enquiry);
    }

    public List<ExportEnquiry> getAllEnquiries() {
        return exportEnquiryRepository.findAll();
    }

    public ExportEnquiry updateStatus(Integer id, String status) {
        ExportEnquiry enquiry = exportEnquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enquiry not found"));
        enquiry.setStatus(status);
        return exportEnquiryRepository.save(enquiry);
    }
}
