package com.mithila.royalpaan.controller;

import com.mithila.royalpaan.dto.MessageResponse;
import com.mithila.royalpaan.dto.OrderRequest;
import com.mithila.royalpaan.entity.Order;
import com.mithila.royalpaan.entity.User;
import com.mithila.royalpaan.security.CustomUserDetails;
import com.mithila.royalpaan.service.OrderService;
import com.mithila.royalpaan.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

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

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        try {
            User customer = getCurrentUser();
            Order order = orderService.createOrder(orderRequest, customer);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Order>> getMyOrders() {
        User customer = getCurrentUser();
        List<Order> orders = orderService.getOrdersByCustomer(customer);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Integer id, @RequestParam String status) {
        try {
            Order order = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
