package com.mithila.royalpaan.repository;

import com.mithila.royalpaan.entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Integer> {
    List<ContactMessage> findByStatus(String status);
}
