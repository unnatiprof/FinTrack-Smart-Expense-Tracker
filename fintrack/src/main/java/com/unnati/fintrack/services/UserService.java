package com.unnati.fintrack.services;

import java.util.List;
import java.util.Optional;

import com.unnati.fintrack.entity.User;

public interface UserService {

    List<User> findAll();

    User findById(Long id);

    Optional<User> findByEmail(String email);

    User save(User user);

    User update(Long id, User user);

    void deleteById(Long id);

    boolean existsByEmail(String email);
}