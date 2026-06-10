package com.mithila.royalpaan.controller;

import com.mithila.royalpaan.entity.SiteSetting;
import com.mithila.royalpaan.repository.SiteSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/settings")
public class SiteSettingController {

    @Autowired
    private SiteSettingRepository siteSettingRepository;

    @GetMapping
    public ResponseEntity<Map<String, String>> getAllSettings() {
        List<SiteSetting> settings = siteSettingRepository.findAll();
        Map<String, String> response = new HashMap<>();
        for (SiteSetting setting : settings) {
            response.put(setting.getKey(), setting.getValue());
        }
        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(response);
    }

    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STAFF')")
    public ResponseEntity<?> updateSettingsBatch(@RequestBody Map<String, String> payload) {
        for (Map.Entry<String, String> entry : payload.entrySet()) {
            SiteSetting setting = siteSettingRepository.findById(entry.getKey())
                    .orElse(new SiteSetting(entry.getKey(), entry.getValue(), "DYNAMIC"));
            setting.setValue(entry.getValue());
            siteSettingRepository.save(setting);
        }
        return ResponseEntity.ok(Map.of("message", "Settings updated successfully"));
    }

    @PostMapping("/sitemap")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STAFF')")
    public ResponseEntity<String> generateSitemap() {
        String sitemap = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
                "  <url>\n" +
                "    <loc>http://localhost:8080/index.html</loc>\n" +
                "    <priority>1.0</priority>\n" +
                "  </url>\n" +
                "  <url>\n" +
                "    <loc>http://localhost:8080/about.html</loc>\n" +
                "    <priority>0.8</priority>\n" +
                "  </url>\n" +
                "  <url>\n" +
                "    <loc>http://localhost:8080/services.html</loc>\n" +
                "    <priority>0.8</priority>\n" +
                "  </url>\n" +
                "  <url>\n" +
                "    <loc>http://localhost:8080/products.html</loc>\n" +
                "    <priority>0.9</priority>\n" +
                "  </url>\n" +
                "  <url>\n" +
                "    <loc>http://localhost:8080/wedding-paan.html</loc>\n" +
                "    <priority>0.9</priority>\n" +
                "  </url>\n" +
                "  <url>\n" +
                "    <loc>http://localhost:8080/event-paan.html</loc>\n" +
                "    <priority>0.8</priority>\n" +
                "  </url>\n" +
                "  <url>\n" +
                "    <loc>http://localhost:8080/gallery.html</loc>\n" +
                "    <priority>0.7</priority>\n" +
                "  </url>\n" +
                "  <url>\n" +
                "    <loc>http://localhost:8080/blog.html</loc>\n" +
                "    <priority>0.7</priority>\n" +
                "  </url>\n" +
                "  <url>\n" +
                "    <loc>http://localhost:8080/contact.html</loc>\n" +
                "    <priority>0.6</priority>\n" +
                "  </url>\n" +
                "</urlset>";
        return ResponseEntity.ok().header("Content-Type", "application/xml").body(sitemap);
    }
}
