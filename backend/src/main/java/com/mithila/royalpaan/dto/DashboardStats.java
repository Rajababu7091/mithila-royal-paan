package com.mithila.royalpaan.dto;

import java.math.BigDecimal;

public class DashboardStats {
    private long totalCustomers;
    private long totalOrders;
    private long totalBookings;
    private BigDecimal totalRevenue;
    private long totalEnquiries;

    public DashboardStats(long totalCustomers, long totalOrders, long totalBookings, BigDecimal totalRevenue, long totalEnquiries) {
        this.totalCustomers = totalCustomers;
        this.totalOrders = totalOrders;
        this.totalBookings = totalBookings;
        this.totalRevenue = totalRevenue;
        this.totalEnquiries = totalEnquiries;
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getTotalEnquiries() {
        return totalEnquiries;
    }

    public void setTotalEnquiries(long totalEnquiries) {
        this.totalEnquiries = totalEnquiries;
    }
}
