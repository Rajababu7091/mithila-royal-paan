package com.mithila.royalpaan.repository;

import com.mithila.royalpaan.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Integer> {
    List<Faq> findAllByOrderByDisplayOrderAsc();
}
