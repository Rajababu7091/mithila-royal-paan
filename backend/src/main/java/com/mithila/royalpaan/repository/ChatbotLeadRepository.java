package com.mithila.royalpaan.repository;

import com.mithila.royalpaan.entity.ChatbotLead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatbotLeadRepository extends JpaRepository<ChatbotLead, Long> {
    List<ChatbotLead> findAllByOrderByCreatedAtDesc();
    List<ChatbotLead> findByStatus(String status);
    List<ChatbotLead> findByCityIgnoreCase(String city);
    long countByStatus(String status);
}
