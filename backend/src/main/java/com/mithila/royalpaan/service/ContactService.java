package com.mithila.royalpaan.service;

import com.mithila.royalpaan.entity.ContactMessage;
import com.mithila.royalpaan.repository.ContactMessageRepository;
import com.mithila.royalpaan.dto.ContactRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    public ContactMessage saveMessage(ContactRequest req) {
        ContactMessage msg = new ContactMessage();
        msg.setName(req.getName());
        msg.setEmail(req.getEmail());
        msg.setPhone(req.getPhone());
        msg.setSubject(req.getSubject());
        msg.setMessage(req.getMessage());
        msg.setStatus("PENDING");

        return contactMessageRepository.save(msg);
    }

    public List<ContactMessage> getAllMessages() {
        return contactMessageRepository.findAll();
    }

    public ContactMessage updateStatus(Integer id, String status) {
        ContactMessage msg = contactMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        msg.setStatus(status);
        return contactMessageRepository.save(msg);
    }
}
