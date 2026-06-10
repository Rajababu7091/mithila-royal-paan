package com.mithila.royalpaan.repository;

import com.mithila.royalpaan.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Integer> {
    List<Service> findAllByOrderByDisplayOrderAsc();
}
