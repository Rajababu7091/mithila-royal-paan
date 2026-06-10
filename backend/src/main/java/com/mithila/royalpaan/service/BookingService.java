package com.mithila.royalpaan.service;

import com.mithila.royalpaan.entity.Booking;
import com.mithila.royalpaan.entity.Notification;
import com.mithila.royalpaan.entity.User;
import com.mithila.royalpaan.repository.BookingRepository;
import com.mithila.royalpaan.repository.NotificationRepository;
import com.mithila.royalpaan.repository.UserRepository;
import com.mithila.royalpaan.dto.BookingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    public Booking createBooking(BookingRequest req, User customer) {
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setEventType(req.getEventType());
        booking.setEventDate(req.getEventDate());
        booking.setLocation(req.getLocation());
        booking.setGuestCount(req.getGuestCount());
        booking.setSpecialRequirements(req.getSpecialRequirements());
        booking.setStatus("PENDING");

        Booking saved = bookingRepository.save(booking);

        // Notify customer
        notificationRepository.save(new Notification(customer, 
            "Your booking request for " + req.getEventType() + " on " + req.getEventDate() + " has been submitted successfully. Status: PENDING."));

        return saved;
    }

    public List<Booking> getBookingsByCustomer(User customer) {
        return bookingRepository.findByCustomerId(customer.getId());
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking updateBookingStatus(Integer id, String status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus(status);
        Booking updated = bookingRepository.save(booking);

        // Notify customer
        notificationRepository.save(new Notification(booking.getCustomer(), 
            "Your wedding/event booking (ID: " + id + ") status has been updated to: " + status + "."));

        return updated;
    }

    public Booking assignStaff(Integer id, Integer staffId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff member not found"));

        booking.setAssignedStaff(staff);
        Booking updated = bookingRepository.save(booking);

        // Notify customer
        notificationRepository.save(new Notification(booking.getCustomer(), 
            "Staff member " + staff.getName() + " has been assigned to coordinate your event booking (ID: " + id + ")."));

        // Notify staff
        notificationRepository.save(new Notification(staff, 
            "You have been assigned to coordinate event booking (ID: " + id + ") at " + booking.getLocation() + " on " + booking.getEventDate() + "."));

        return updated;
    }
}
