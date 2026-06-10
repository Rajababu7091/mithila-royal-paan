package com.mithila.royalpaan.service;

import com.mithila.royalpaan.entity.Gallery;
import com.mithila.royalpaan.repository.GalleryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GalleryService {

    @Autowired
    private GalleryRepository galleryRepository;

    public List<Gallery> getAllGalleryItems() {
        return galleryRepository.findAll();
    }

    public List<Gallery> getGalleryItemsByCategory(String category) {
        return galleryRepository.findByCategory(category);
    }

    public Gallery saveGalleryItem(Gallery item) {
        return galleryRepository.save(item);
    }

    public void deleteGalleryItem(Integer id) {
        galleryRepository.deleteById(id);
    }
}
