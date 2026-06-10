package com.mithila.royalpaan.service;

import com.mithila.royalpaan.entity.Notification;
import com.mithila.royalpaan.entity.User;
import com.mithila.royalpaan.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public List<Notification> getNotificationsByUser(User user) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(user.getId());
    }

    public Notification markAsRead(Integer id, User user) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized notification edit request");
        }

        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }

    public void markAllAsRead(User user) {
        List<Notification> unread = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(user.getId());
        for (Notification n : unread) {
            n.setIsRead(true);
        }
        notificationRepository.saveAll(unread);
    }
}
