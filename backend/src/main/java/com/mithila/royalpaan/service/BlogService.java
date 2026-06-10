package com.mithila.royalpaan.service;

import com.mithila.royalpaan.entity.Blog;
import com.mithila.royalpaan.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    public List<Blog> getAllBlogs() {
        return blogRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Blog> getBlogById(Integer id) {
        return blogRepository.findById(id);
    }

    public Blog saveBlog(Blog blog) {
        return blogRepository.save(blog);
    }

    public void deleteBlog(Integer id) {
        blogRepository.deleteById(id);
    }
}
