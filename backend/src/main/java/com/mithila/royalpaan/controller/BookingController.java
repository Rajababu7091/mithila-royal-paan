package com.mithila.royalpaan.controller;

import com.mithila.royalpaan.dto.BookingRequest;
import com.mithila.royalpaan.dto.MessageResponse;
import com.mithila.royalpaan.entity.Booking;
import com.mithila.royalpaan.entity.User;
import com.mithila.royalpaan.security.CustomUserDetails;
import com.mithila.royalpaan.service.BookingService;
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
@RequestMapping("/api/v1/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

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
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingRequest bookingRequest) {
        try {
            User customer = getCurrentUser();
            Booking booking = bookingService.createBooking(bookingRequest, customer);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getMyBookings() {
        User customer = getCurrentUser();
        List<Booking> bookings = bookingService.getBookingsByCustomer(customer);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> updateBookingStatus(@PathVariable Integer id, @RequestParam String status) {
        try {
            Booking booking = bookingService.updateBookingStatus(id, status);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> assignStaff(@PathVariable Integer id, @RequestParam Integer staffId) {
        try {
            Booking booking = bookingService.assignStaff(id, staffId);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
