package com.gan.wcare.user.service;

import com.gan.wcare.user.dto.LoginRequest;
import com.gan.wcare.user.dto.LoginResponse;
import com.gan.wcare.user.dto.UserDTO;
import com.gan.wcare.user.model.User;
import com.gan.wcare.user.repository.UserRepository;
import com.gan.wcare.user.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * User Service - Business logic for user management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Authenticate user and generate JWT token
     */
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Login attempt for username: {}", loginRequest.getUsername());

        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());
        
        if (userOpt.isEmpty()) {
            log.warn("User not found: {}", loginRequest.getUsername());
            return LoginResponse.builder()
                    .message("Invalid username or password")
                    .build();
        }

        User user = userOpt.get();
        
        if (!user.getActive()) {
            log.warn("Inactive user login attempt: {}", loginRequest.getUsername());
            return LoginResponse.builder()
                    .message("User account is inactive")
                    .build();
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.warn("Invalid password for user: {}", loginRequest.getUsername());
            return LoginResponse.builder()
                    .message("Invalid username or password")
                    .build();
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(
                user.getUsername(), 
                user.getRole().name(), 
                user.getId()
        );

        // Determine profile ID based on role
        Long profileId = getProfileId(user);

        log.info("Successful login for user: {} with role: {}", user.getUsername(), user.getRole());

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .emailId(user.getEmailId())
                .role(user.getRole())
                .profileId(profileId)
                .message("Login successful")
                .build();
    }

    /**
     * Get profile ID based on user role
     */
    private Long getProfileId(User user) {
        return switch (user.getRole()) {
            case BUSINESS_MANAGER -> user.getBusinessManagerId();
            case WEALTH_MANAGER -> user.getWealthManagerId();
            case CUSTOMER -> user.getCustomerId();
        };
    }

    /**
     * Create new user
     */
    public UserDTO createUser(UserDTO userDTO) {
        log.info("Creating new user: {}", userDTO.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmailId(userDTO.getEmailId())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmailId(userDTO.getEmailId());
        user.setRole(userDTO.getRole());
        user.setActive(true);
        user.setBusinessManagerId(userDTO.getBusinessManagerId());
        user.setWealthManagerId(userDTO.getWealthManagerId());
        user.setCustomerId(userDTO.getCustomerId());

        User savedUser = userRepository.save(user);
        log.info("User created successfully: {}", savedUser.getUsername());

        return convertToDTO(savedUser);
    }

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDTO);
    }

    /**
     * Get user by username
     */
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToDTO);
    }

    /**
     * Get all users
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update user
     */
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        log.info("Updating user: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update fields (excluding password and username)
        user.setEmailId(userDTO.getEmailId());
        user.setRole(userDTO.getRole());
        user.setActive(userDTO.getActive());
        user.setBusinessManagerId(userDTO.getBusinessManagerId());
        user.setWealthManagerId(userDTO.getWealthManagerId());
        user.setCustomerId(userDTO.getCustomerId());

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: {}", updatedUser.getUsername());

        return convertToDTO(updatedUser);
    }

    /**
     * Update user profile IDs (called by other services)
     */
    public void updateUserProfileId(Long userId, User.UserRole role, Long profileId) {
        log.info("Updating profile ID for user: {} with role: {} and profileId: {}", userId, role, profileId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        switch (role) {
            case BUSINESS_MANAGER -> user.setBusinessManagerId(profileId);
            case WEALTH_MANAGER -> user.setWealthManagerId(profileId);
            case CUSTOMER -> user.setCustomerId(profileId);
        }

        userRepository.save(user);
        log.info("Profile ID updated successfully for user: {}", userId);
    }

    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    /**
     * Get username from token
     */
    public String getUsernameFromToken(String token) {
        return jwtUtil.getUsernameFromToken(token);
    }

    /**
     * Get role from token
     */
    public String getRoleFromToken(String token) {
        return jwtUtil.getRoleFromToken(token);
    }

    /**
     * Get user ID from token
     */
    public Long getUserIdFromToken(String token) {
        return jwtUtil.getUserIdFromToken(token);
    }

    /**
     * Convert User entity to DTO
     */
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .emailId(user.getEmailId())
                .role(user.getRole())
                .active(user.getActive())
                .businessManagerId(user.getBusinessManagerId())
                .wealthManagerId(user.getWealthManagerId())
                .customerId(user.getCustomerId())
                .build();
    }
}

// Made with Bob
