package com.mithila.royalpaan.repository;

import com.mithila.royalpaan.entity.ChatbotConversation;
import com.mithila.royalpaan.entity.ChatbotMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatbotMessageRepository extends JpaRepository<ChatbotMessage, Long> {
    List<ChatbotMessage> findByConversationOrderByCreatedAtAsc(ChatbotConversation conversation);
    List<ChatbotMessage> findTop20ByConversationOrderByCreatedAtAsc(ChatbotConversation conversation);
    long countByConversation(ChatbotConversation conversation);
}
