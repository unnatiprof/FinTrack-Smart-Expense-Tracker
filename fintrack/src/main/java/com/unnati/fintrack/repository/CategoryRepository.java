package com.unnati.fintrack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unnati.fintrack.entity.Category;
import com.unnati.fintrack.enums.CategoryStatus;
import com.unnati.fintrack.enums.CategoryType;


public interface CategoryRepository extends JpaRepository<Category, Long>
{
      List<Category> findByCategory(CategoryType category);
      
      List<Category> findByStatus(CategoryStatus status);

      Optional<Category> findByNameIgnoreCase(String name);

      List<Category> findByNameContainingIgnoreCase(String keyword);

      boolean existsByNameIgnoreCase(String name);
}
