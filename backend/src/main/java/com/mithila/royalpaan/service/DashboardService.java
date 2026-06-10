package com.mithila.royalpaan.service;

import com.mithila.royalpaan.dto.DashboardStats;
import com.mithila.royalpaan.entity.Order;
import com.mithila.royalpaan.repository.BookingRepository;
import com.mithila.royalpaan.repository.ExportEnquiryRepository;
import com.mithila.royalpaan.repository.OrderRepository;
import com.mithila.royalpaan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ExportEnquiryRepository exportEnquiryRepository;

    public DashboardStats getStats() {
        // Count users with ROLE_CUSTOMER
        long totalCustomers = userRepository.findByRoleName("ROLE_CUSTOMER").size();
        
        long totalOrders = orderRepository.count();
        long totalBookings = bookingRepository.count();
        long totalEnquiries = exportEnquiryRepository.count();

        // Calculate revenue (Sum of prices of all orders not CANCELLED)
        List<Order> orders = orderRepository.findAll();
        BigDecimal totalRevenue = orders.stream()
                .filter(order -> ! "CANCELLED".equalsIgnoreCase(order.getStatus()))
                .map(Order::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardStats(totalCustomers, totalOrders, totalBookings, totalRevenue, totalEnquiries);
    }
}
