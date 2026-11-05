
package com.unnati.fintrack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unnati.fintrack.entity.Category;
import com.unnati.fintrack.enums.CategoryStatus;
import com.unnati.fintrack.enums.CategoryType;

public interface CategoryRepository
        extends JpaRepository<Category, Long> {

    // Existing category filters
    List<Category> findByCategory(CategoryType category);

    List<Category> findByStatus(CategoryStatus status);

    Optional<Category> findByNameIgnoreCase(String name);

    List<Category> findByNameContainingIgnoreCase(String keyword);

    // User-owned categories
    List<Category> findByUser_Email(String email);

    Optional<Category> findByIdAndUser_Email(
            Long id, String email);

    // Global categories
    List<Category> findByUserIsNull();

    Optional<Category> findByIdAndUserIsNull(Long id);

    // Global + current user's categories
    List<Category> findByUser_EmailOrUserIsNull(String email);

    // Duplicate checks
    boolean existsByNameIgnoreCaseAndUser_Email(
            String name, String email);

    boolean existsByNameIgnoreCase(String name);
}
