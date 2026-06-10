package com.mithila.royalpaan.service;

import com.mithila.royalpaan.entity.Testimonial;
import com.mithila.royalpaan.repository.TestimonialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestimonialService {

    @Autowired
    private TestimonialRepository testimonialRepository;

    public List<Testimonial> getAllTestimonials() {
        return testimonialRepository.findAll();
    }

    public Testimonial saveTestimonial(Testimonial testimonial) {
        return testimonialRepository.save(testimonial);
    }

    public void deleteTestimonial(Integer id) {
        testimonialRepository.deleteById(id);
    }
}
