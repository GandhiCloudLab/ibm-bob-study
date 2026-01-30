package com.gan.wcare.goal.service;

import com.gan.wcare.goal.dto.GoalDTO;
import com.gan.wcare.goal.model.Goal;
import com.gan.wcare.goal.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Goal Service - Business logic for goal management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GoalService {

    private final GoalRepository goalRepository;

    /**
     * Get all goals
     */
    @Transactional(readOnly = true)
    public List<GoalDTO> getAllGoals() {
        return goalRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get goal by ID
     */
    @Transactional(readOnly = true)
    public Optional<GoalDTO> getGoalById(Long id) {
        return goalRepository.findById(id)
                .map(this::convertToDTO);
    }

    /**
     * Get goals by customer ID
     */
    @Transactional(readOnly = true)
    public List<GoalDTO> getGoalsByCustomerId(Long customerId) {
        return goalRepository.findByCustomerId(customerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get goals by wealth manager ID
     */
    @Transactional(readOnly = true)
    public List<GoalDTO> getGoalsByWealthManagerId(Long wealthManagerId) {
        return goalRepository.findByWealthManagerId(wealthManagerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create new goal
     */
    public GoalDTO createGoal(GoalDTO goalDTO) {
        log.info("Creating new goal for customer ID: {}", goalDTO.getCustomerId());

        Goal goal = convertToEntity(goalDTO);
        Goal savedGoal = goalRepository.save(goal);

        log.info("Goal created successfully with ID: {}", savedGoal.getId());
        return convertToDTO(savedGoal);
    }

    /**
     * Update goal
     */
    public GoalDTO updateGoal(Long id, GoalDTO goalDTO) {
        log.info("Updating goal: {}", id);

        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        // Update fields
        goal.setCustomerId(goalDTO.getCustomerId());
        goal.setWealthManagerId(goalDTO.getWealthManagerId());
        goal.setGoalReference(goalDTO.getGoalReference());
        goal.setGoalDescription(goalDTO.getGoalDescription());
        goal.setStartDate(goalDTO.getStartDate());
        goal.setTargetDate(goalDTO.getTargetDate());
        goal.setTargetAmount(goalDTO.getTargetAmount());

        Goal updatedGoal = goalRepository.save(goal);
        log.info("Goal updated successfully: {}", updatedGoal.getId());

        return convertToDTO(updatedGoal);
    }

    /**
     * Delete goal
     */
    public void deleteGoal(Long id) {
        log.info("Deleting goal: {}", id);
        
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        
        goalRepository.delete(goal);
        log.info("Goal deleted successfully: {}", id);
    }

    /**
     * Calculate goal progress
     */
    public GoalDTO calculateProgress(Long id, Double currentAmount) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        GoalDTO dto = convertToDTO(goal);
        dto.setCurrentAmount(currentAmount);

        if (goal.getTargetAmount() != null && goal.getTargetAmount() > 0) {
            double progress = (currentAmount / goal.getTargetAmount()) * 100;
            dto.setProgressPercentage(Math.min(progress, 100.0));
        }

        if (goal.getTargetDate() != null) {
            long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), goal.getTargetDate());
            dto.setDaysRemaining(Math.max(daysRemaining, 0));
        }

        return dto;
    }

    /**
     * Convert Goal entity to DTO
     */
    private GoalDTO convertToDTO(Goal goal) {
        GoalDTO dto = GoalDTO.builder()
                .id(goal.getId())
                .customerId(goal.getCustomerId())
                .wealthManagerId(goal.getWealthManagerId())
                .goalReference(goal.getGoalReference())
                .goalDescription(goal.getGoalDescription())
                .startDate(goal.getStartDate())
                .targetDate(goal.getTargetDate())
                .targetAmount(goal.getTargetAmount())
                .build();

        // Calculate days remaining
        if (goal.getTargetDate() != null) {
            long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), goal.getTargetDate());
            dto.setDaysRemaining(Math.max(daysRemaining, 0));
        }

        return dto;
    }

    /**
     * Convert DTO to Goal entity
     */
    private Goal convertToEntity(GoalDTO dto) {
        Goal goal = new Goal();
        goal.setCustomerId(dto.getCustomerId());
        goal.setWealthManagerId(dto.getWealthManagerId());
        goal.setGoalReference(dto.getGoalReference());
        goal.setGoalDescription(dto.getGoalDescription());
        goal.setStartDate(dto.getStartDate());
        goal.setTargetDate(dto.getTargetDate());
        goal.setTargetAmount(dto.getTargetAmount());
        return goal;
    }
}

// Made with Bob
