package com.gan.wcare.user.dto;

import com.gan.wcare.user.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Data Transfer Object
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String emailId;

    @NotNull(message = "Role is required")
    private User.UserRole role;

    private Boolean active;

    private Long businessManagerId;
    private Long wealthManagerId;
    private Long customerId;

    // Password only for creation, not returned
    private String password;
}

// Made with Bob
