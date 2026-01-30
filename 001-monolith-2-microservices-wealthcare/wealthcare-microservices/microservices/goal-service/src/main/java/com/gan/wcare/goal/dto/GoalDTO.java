package com.gan.wcare.goal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Goal Data Transfer Object
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalDTO {

    private Long id;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Wealth Manager ID is required")
    private Long wealthManagerId;

    private String goalReference;

    @NotBlank(message = "Goal description is required")
    private String goalDescription;

    private LocalDate startDate;

    private LocalDate targetDate;

    private Double targetAmount;

    // Additional fields for display
    private String customerName;
    private Double currentAmount;
    private Double progressPercentage;
    private Long daysRemaining;
}

// Made with Bob
