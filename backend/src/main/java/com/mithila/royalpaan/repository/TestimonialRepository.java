package com.mithila.royalpaan.repository;

import com.mithila.royalpaan.entity.Testimonial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestimonialRepository extends JpaRepository<Testimonial, Integer> {
}
