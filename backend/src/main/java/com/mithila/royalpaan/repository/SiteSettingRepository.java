package com.mithila.royalpaan.repository;

import com.mithila.royalpaan.entity.SiteSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SiteSettingRepository extends JpaRepository<SiteSetting, String> {
    List<SiteSetting> findByCategory(String category);
}
