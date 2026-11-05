package com.unnati.fintrack.services.impl;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.unnati.fintrack.entity.Category;
import com.unnati.fintrack.entity.User;
import com.unnati.fintrack.exception.ResourceNotFoundException;
import com.unnati.fintrack.repository.CategoryRepository;
import com.unnati.fintrack.services.CategoryService;
import com.unnati.fintrack.services.UserService;

@Service
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

    @Override
    public List<Category> findAll() {
        String email = getCurrentUserEmail();
        return categoryRepository.findByUser_EmailOrUserIsNull(email);
    }

    @Override
    public Category findById(Long id) {
        String email = getCurrentUserEmail();

        return categoryRepository.findByIdAndUser_Email(id, email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));
    }

    @Override
    public Category save(Category category) {
        String email = getCurrentUserEmail();

        User user = userService.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        category.setId(null);
        category.setUser(user);

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