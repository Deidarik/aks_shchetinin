package com.vocab.service;

import com.vocab.dto.ChangeMessage;
import com.vocab.entity.Category;
import com.vocab.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private JmsNotificationService jmsService; // <-- ДОБАВЬ

    public List<Category> findAll() {
        return categoryRepository.findAllByOrderByNameAsc();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public Page<Category> findAllPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        return categoryRepository.findAllByOrderByNameAsc(pageable);
    }

    public Category create(String name, String description) {
        Category category = new Category();
        category.setName(name.trim());
        category.setDescription(description);
        Category saved = categoryRepository.save(category);

        ChangeMessage msg = new ChangeMessage(
                "INSERT", "Category", saved.getId(),
                "name", null, name
        );
        jmsService.sendAuditMessage(msg);
        return saved;
    }

    public Category update(Long id, String name, String description) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));
        String oldName = category.getName();
        category.setName(name.trim());
        category.setDescription(description);
        categoryRepository.save(category);

        ChangeMessage msg = new ChangeMessage(
                "UPDATE", "Category", id,
                "name", oldName, name
        );
        jmsService.sendAuditMessage(msg);
        return category;
    }

    public void delete(Long id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category != null) {
            categoryRepository.deleteById(id);
            ChangeMessage msg = new ChangeMessage(
                    "DELETE", "Category", id,
                    "name", category.getName(), null
            );
            jmsService.sendAuditMessage(msg);
            jmsService.sendEmailNotification(msg);
        }
    }
}