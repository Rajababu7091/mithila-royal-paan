package com.mithila.royalpaan.repository;

import com.mithila.royalpaan.entity.Gallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Integer> {
    List<Gallery> findByCategory(String category);
}
