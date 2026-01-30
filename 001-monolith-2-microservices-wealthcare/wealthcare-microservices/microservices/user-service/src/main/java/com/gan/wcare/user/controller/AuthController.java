package com.gan.wcare.user.controller;

import com.gan.wcare.user.dto.LoginRequest;
import com.gan.wcare.user.dto.LoginResponse;
import com.gan.wcare.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login request received for username: {}", loginRequest.getUsername());
        LoginResponse response = userService.login(loginRequest);
        
        if (response.getToken() != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate token", description = "Validate JWT token")
    public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
        boolean isValid = userService.validateToken(token);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/user-info")
    @Operation(summary = "Get user info from token", description = "Extract user information from JWT token")
    public ResponseEntity<?> getUserInfo(@RequestParam String token) {
        if (!userService.validateToken(token)) {
            return ResponseEntity.badRequest().body("Invalid token");
        }

        String username = userService.getUsernameFromToken(token);
        String role = userService.getRoleFromToken(token);
        Long userId = userService.getUserIdFromToken(token);

        return ResponseEntity.ok(new UserInfo(userId, username, role));
    }

    // Inner class for user info response
    record UserInfo(Long userId, String username, String role) {}
}

// Made with Bob
