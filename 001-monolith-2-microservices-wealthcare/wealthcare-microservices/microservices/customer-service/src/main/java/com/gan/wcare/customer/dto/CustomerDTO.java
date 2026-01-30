package com.gan.wcare.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Customer Data Transfer Object
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDTO {

    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Wealth Manager ID is required")
    private Long wealthManagerId;

    // Personal Information
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String gender;
    private Integer age;
    private Double avgIncome;

    // Marital Status
    private Boolean married;

    // Spouse Information
    private String spouseFirstName;
    private String spouseLastName;
    private String spouseGender;
    private Integer spouseAge;
    private Double spouseAvgIncome;

    // Children Information
    private Integer noOfChildren;
    private String child1FirstName;
    private String child1LastName;
    private String child1Gender;
    private Integer child1Age;
    private String child2FirstName;
    private String child2LastName;
    private String child2Gender;
    private Integer child2Age;

    // Contact Information
    private String city;
    private String phone;

    @Email(message = "Email should be valid")
    private String emailId;

    private String country;
    private String zipCode;
    private LocalDate startDate;

    // Additional fields for display
    private String wealthManagerName;
    private String imageUrl;
}

// Made with Bob
