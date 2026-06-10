package com.mithila.royalpaan.repository;

import com.mithila.royalpaan.entity.ExportEnquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExportEnquiryRepository extends JpaRepository<ExportEnquiry, Integer> {
    List<ExportEnquiry> findByStatus(String status);
}
