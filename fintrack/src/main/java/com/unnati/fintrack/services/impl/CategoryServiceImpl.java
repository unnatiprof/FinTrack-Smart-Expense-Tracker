package com.unnati.fintrack.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.unnati.fintrack.entity.Category;
import com.unnati.fintrack.repository.CategoryRepository;
import com.unnati.fintrack.services.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category update(Long id, Category category) {
        Category existingCategory = findById(id);

        existingCategory.setName(category.getName());
        existingCategory.setCategory(category.getCategory());
        existingCategory.setColorCode(category.getColorCode());
        existingCategory.setIcon(category.getIcon());
        existingCategory.setStatus(category.getStatus());

        return categoryRepository.save(existingCategory);
    }

    @Override
    public void deleteById(Long id) {
        Category existingCategory = findById(id);
        categoryRepository.delete(existingCategory);
    }
}