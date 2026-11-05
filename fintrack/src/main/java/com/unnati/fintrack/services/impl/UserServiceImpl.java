
package com.unnati.fintrack.services.impl;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unnati.fintrack.dto.request.ChangePasswordRequest;
import com.unnati.fintrack.dto.request.ProfileUpdateRequest;
import com.unnati.fintrack.dto.response.ProfileResponse;
import com.unnati.fintrack.entity.User;
import com.unnati.fintrack.events.UserRegisteredEvent;
import com.unnati.fintrack.exception.ResourceNotFoundException;
import com.unnati.fintrack.repository.UserRepository;
import com.unnati.fintrack.services.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            ApplicationEventPublisher eventPublisher,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.passwordEncoder = passwordEncoder;
    }

    // ---------------------------------------------------------
    // FIND ALL USERS
    // ---------------------------------------------------------

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // ---------------------------------------------------------
    // FIND USER BY ID
    // ---------------------------------------------------------

    @Override
    public User findById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id));
    }

    // ---------------------------------------------------------
    // FIND USER BY EMAIL
    // ---------------------------------------------------------

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // ---------------------------------------------------------
    // SAVE / REGISTER USER
    // ---------------------------------------------------------

    @Override
    public User save(User user) {

        String email = user.getEmail().trim().toLowerCase(Locale.ROOT);
        user.setEmail(email);

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Email already exists: " + email);
        }

        User savedUser = userRepository.save(user);

        eventPublisher.publishEvent(
                new UserRegisteredEvent(
                        savedUser.getId(),
                        savedUser.getName(),
                        savedUser.getEmail()));

        return savedUser;
    }

    // ---------------------------------------------------------
    // UPDATE USER
    // ---------------------------------------------------------

    @Override
    public User update(Long id, User user) {

        User existingUser = findById(id);

        String newEmail = user.getEmail().trim().toLowerCase(Locale.ROOT);

        if (!existingUser.getEmail().equalsIgnoreCase(newEmail)
                && userRepository.existsByEmail(newEmail)) {

            throw new IllegalArgumentException(
                    "Email already exists: " + newEmail);
        }

        existingUser.setName(user.getName());
        existingUser.setEmail(newEmail);
        existingUser.setPassword(user.getPassword());
        existingUser.setRole(user.getRole());
        existingUser.setStatus(user.getStatus());

        return userRepository.save(existingUser);
    }

    // ---------------------------------------------------------
    // DELETE USER
    // ---------------------------------------------------------

    @Override
    public void deleteById(Long id) {

        User existingUser = findById(id);
        userRepository.delete(existingUser);
    }

    // ---------------------------------------------------------
    // CHECK EMAIL
    // ---------------------------------------------------------

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // ---------------------------------------------------------
    // GET LOGGED-IN USER PROFILE
    // ---------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getProfile(String email) {

        User user = getUserByEmail(email);

        return buildProfileResponse(user);
    }

    // ---------------------------------------------------------
    // UPDATE LOGGED-IN USER PROFILE
    // ---------------------------------------------------------

    @Override
    @Transactional
    public ProfileResponse updateProfile(
            String email,
            ProfileUpdateRequest request) {

        User user = getUserByEmail(email);

        String newEmail = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        // Check whether the requested email belongs to another user.
        if (!user.getEmail().equalsIgnoreCase(newEmail)
                && userRepository.existsByEmail(newEmail)) {

            throw new IllegalArgumentException(
                    "Email already exists: " + newEmail);
        }

        user.setName(request.getName().trim());
        user.setEmail(newEmail);
        user.setCurrency(request.getCurrency());
        user.setMonthlyIncome(request.getMonthlyIncome());

        User updatedUser = userRepository.save(user);

        return buildProfileResponse(updatedUser);
    }

    // ---------------------------------------------------------
    // CHANGE PASSWORD
    // ---------------------------------------------------------

    @Override
    @Transactional
    public void changePassword(
            String email,
            ChangePasswordRequest request) {

        User user = getUserByEmail(email);

        // Verify the current password.
        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {

            throw new IllegalArgumentException(
                    "Current password is incorrect");
        }

        // Check that the new password and confirmation match.
        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            throw new IllegalArgumentException(
                    "New password and confirm password do not match");
        }

        // Prevent reusing the current password.
        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword())) {

            throw new IllegalArgumentException(
                    "New password must be different from current password");
        }

        // Store only the encoded password.
        user.setPassword(
                passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }

    // ---------------------------------------------------------
    // COMMON HELPER: FIND USER BY EMAIL
    // ---------------------------------------------------------

    private User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: " + email));
    }

    // ---------------------------------------------------------
    // COMMON HELPER: BUILD PROFILE RESPONSE
    // ---------------------------------------------------------

    private ProfileResponse buildProfileResponse(User user) {

        return new ProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCurrency(),
                user.getMonthlyIncome(),
                user.getRole() != null ? user.getRole().name() : null,
                user.getStatus() != null ? user.getStatus().name() : null,
                user.getLastLogin(),
                user.getCreatedAt()
        );
    }
}
