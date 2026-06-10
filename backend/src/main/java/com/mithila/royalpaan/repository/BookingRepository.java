package com.mithila.royalpaan.repository;

import com.mithila.royalpaan.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByCustomerId(Integer customerId);
    List<Booking> findByStatus(String status);
    List<Booking> findByAssignedStaffId(Integer staffId);
}
