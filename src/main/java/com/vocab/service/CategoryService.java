package com.vocab.service;

import com.vocab.entity.Category;
import com.vocab.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    // ДОБАВЬ ЭТОТ МЕТОД:
    public Category findById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public Category create(String name, String description) {
        Category category = new Category();
        category.setName(name.trim());
        category.setDescription(description);
        return categoryRepository.save(category);
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    public Page<Category> findAllPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        return categoryRepository.findAllByOrderByNameAsc(pageable);
    }
}