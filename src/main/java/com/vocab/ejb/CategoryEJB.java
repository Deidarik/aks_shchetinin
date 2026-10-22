package com.vocab.ejb;

import com.vocab.entity.Category;
import jakarta.ejb.EJBException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class CategoryEJB {

    @PersistenceContext(unitName = "vocabPU")
    private EntityManager em;

    public List<Category> findAll() {
        return em.createQuery("select c from Category c order by c.name", Category.class).getResultList();
    }

    public Category find(Long id) {
        return id == null ? null : em.find(Category.class, id);
    }

    public Long create(String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new EJBException("Название категории обязательно");
        }
        Category category = new Category();
        category.setName(name.trim());
        category.setDescription(description != null ? description.trim() : "");
        em.persist(category);
        return category.getId();
    }

    public void update(Long id, String name, String description) {
        Category category = em.find(Category.class, id);
        if (category == null) {
            throw new EJBException("Категория не найдена: " + id);
        }
        if (name == null || name.trim().isEmpty()) {
            throw new EJBException("Название категории обязательно");
        }
        category.setName(name.trim());
        category.setDescription(description != null ? description.trim() : "");
    }

    public void delete(Long id) {
        Category category = em.find(Category.class, id);
        if (category != null) {
            try {
                em.remove(category);
            } catch (Exception e) {
                throw new EJBException("Невозможно удалить категорию: в ней есть слова. Сначала удалите или перенесите слова.");
            }
        }
    }
}