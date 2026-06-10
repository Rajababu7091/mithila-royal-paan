package com.mithila.royalpaan.controller;

import com.mithila.royalpaan.dto.MessageResponse;
import com.mithila.royalpaan.entity.Notification;
import com.mithila.royalpaan.entity.User;
import com.mithila.royalpaan.security.CustomUserDetails;
import com.mithila.royalpaan.service.NotificationService;
import com.mithila.royalpaan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() instanceof String) {
            throw new RuntimeException("Unauthorized: User is not authenticated");
        }
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getMyNotifications() {
        User user = getCurrentUser();
        List<Notification> notifications = notificationService.getNotificationsByUser(user);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getMyUnreadNotifications() {
        User user = getCurrentUser();
        List<Notification> notifications = notificationService.getUnreadNotifications(user);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Integer id) {
        try {
            User user = getCurrentUser();
            Notification n = notificationService.markAsRead(id, user);
            return ResponseEntity.ok(n);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PutMapping("/read-all")
    public ResponseEntity<?> readAll() {
        try {
            User user = getCurrentUser();
            notificationService.markAllAsRead(user);
            return ResponseEntity.ok(new MessageResponse("All notifications marked as read"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
