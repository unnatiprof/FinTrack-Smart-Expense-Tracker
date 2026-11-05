
package com.unnati.fintrack.services.impl;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unnati.fintrack.entity.Category;
import com.unnati.fintrack.entity.User;
import com.unnati.fintrack.exception.ResourceNotFoundException;
import com.unnati.fintrack.repository.CategoryRepository;
import com.unnati.fintrack.services.CategoryService;
import com.unnati.fintrack.services.UserService;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository,
            UserService userService) {

        this.categoryRepository = categoryRepository;
        this.userService = userService;
    }

    private String getCurrentUserEmail() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {

            throw new AccessDeniedException("User not authenticated");
        }

        return authentication.getName();
    }

    private Category findOwnedCategory(Long id, String email) {

        return categoryRepository.findByIdAndUser_Email(id, email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));
    }

    // GET ALL: Global + current user's categories
    @Override
    @Transactional(readOnly = true)
    public List<Category> findAll() {

        String email = getCurrentUserEmail();

        return categoryRepository.findByUser_EmailOrUserIsNull(email);
    }

    // GET BY ID: Own category or global category
    @Override
    @Transactional(readOnly = true)
    public Category findById(Long id) {

        String email = getCurrentUserEmail();

        return categoryRepository.findByIdAndUser_Email(id, email)
                .or(() -> categoryRepository.findByIdAndUserIsNull(id))
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));
    }

    // CREATE: Ownership is assigned by the backend
    @Override
    public Category save(Category category) {

        String email = getCurrentUserEmail();

        User user = userService.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (category.getName() == null
                || category.getName().isBlank()) {
            throw new IllegalArgumentException("Category name is required");
        }

        // Prevent duplicate names among this user's categories
        if (categoryRepository.existsByNameIgnoreCaseAndUser_Email(
                category.getName().trim(), email)) {
            throw new IllegalArgumentException(
                    "You already have a category with this name");
        }

        category.setId(null);
        category.setName(category.getName().trim());
        category.setUser(user);

        return categoryRepository.save(category);
    }

    // UPDATE: Only the owner can update a private category
    @Override
    public Category update(Long id, Category category) {

        String email = getCurrentUserEmail();

        Category existingCategory = findOwnedCategory(id, email);

        if (category.getName() == null
                || category.getName().isBlank()) {
            throw new IllegalArgumentException("Category name is required");
        }

        existingCategory.setName(category.getName().trim());
        existingCategory.setCategory(category.getCategory());
        existingCategory.setColorCode(category.getColorCode());
        existingCategory.setIcon(category.getIcon());
        existingCategory.setStatus(category.getStatus());

        return categoryRepository.save(existingCategory);
    }

    // DELETE: Only the owner can delete a private category
    @Override
    public void deleteById(Long id) {

        String email = getCurrentUserEmail();

        Category existingCategory = findOwnedCategory(id, email);

        categoryRepository.delete(existingCategory);
    }
}
