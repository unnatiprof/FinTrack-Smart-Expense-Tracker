package com.unnati.fintrack.services;

import java.util.List;

import com.unnati.fintrack.entity.Category;

public interface CategoryService {

    List<Category> findAll();

    Category findById(Long id);

    Category save(Category category);

    Category update(Long id, Category category);

    void deleteById(Long id);
}