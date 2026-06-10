package com.mithila.royalpaan.repository;

import com.mithila.royalpaan.entity.WeddingPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WeddingPackageRepository extends JpaRepository<WeddingPackage, Integer> {
    List<WeddingPackage> findAllByOrderByDisplayOrderAsc();
}
