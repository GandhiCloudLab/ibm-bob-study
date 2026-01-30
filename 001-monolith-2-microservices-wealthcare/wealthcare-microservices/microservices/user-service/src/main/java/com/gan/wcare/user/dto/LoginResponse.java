package com.gan.wcare.user.dto;

import com.gan.wcare.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Login Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String token;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private String emailId;
    private User.UserRole role;
    private Long profileId;  // businessManagerId, wealthManagerId, or customerId
    private String message;
}

// Made with Bob
