package com.mithila.royalpaan.repository;

import com.mithila.royalpaan.entity.ChatbotConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ChatbotConversationRepository extends JpaRepository<ChatbotConversation, Long> {
    Optional<ChatbotConversation> findBySessionId(String sessionId);
    List<ChatbotConversation> findAllByOrderByCreatedAtDesc();
    List<ChatbotConversation> findByStatus(String status);
    long countByLeadGeneratedTrue();
}
