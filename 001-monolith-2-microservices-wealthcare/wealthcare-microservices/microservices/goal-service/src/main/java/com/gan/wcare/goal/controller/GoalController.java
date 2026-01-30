package com.gan.wcare.goal.controller;

import com.gan.wcare.goal.dto.GoalDTO;
import com.gan.wcare.goal.service.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Goal Controller
 */
@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Goal Management", description = "Financial goal management APIs")
public class GoalController {

    private final GoalService goalService;

    @GetMapping
    @Operation(summary = "Get all goals", description = "Retrieve all financial goals")
    public ResponseEntity<List<GoalDTO>> getAllGoals() {
        List<GoalDTO> goals = goalService.getAllGoals();
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get goal by ID", description = "Retrieve goal by ID")
    public ResponseEntity<GoalDTO> getGoalById(@PathVariable Long id) {
        Optional<GoalDTO> goal = goalService.getGoalById(id);
        return goal.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get goals by customer", description = "Retrieve all goals for a customer")
    public ResponseEntity<List<GoalDTO>> getGoalsByCustomerId(@PathVariable Long customerId) {
        List<GoalDTO> goals = goalService.getGoalsByCustomerId(customerId);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/wealth-manager/{wealthManagerId}")
    @Operation(summary = "Get goals by wealth manager", description = "Retrieve all goals managed by a wealth manager")
    public ResponseEntity<List<GoalDTO>> getGoalsByWealthManagerId(@PathVariable Long wealthManagerId) {
        List<GoalDTO> goals = goalService.getGoalsByWealthManagerId(wealthManagerId);
        return ResponseEntity.ok(goals);
    }

    @PostMapping
    @Operation(summary = "Create goal", description = "Create a new financial goal")
    public ResponseEntity<GoalDTO> createGoal(@Valid @RequestBody GoalDTO goalDTO) {
        try {
            GoalDTO createdGoal = goalService.createGoal(goalDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdGoal);
        } catch (RuntimeException e) {
            log.error("Error creating goal: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update goal", description = "Update existing goal")
    public ResponseEntity<GoalDTO> updateGoal(@PathVariable Long id, @Valid @RequestBody GoalDTO goalDTO) {
        try {
            GoalDTO updatedGoal = goalService.updateGoal(id, goalDTO);
            return ResponseEntity.ok(updatedGoal);
        } catch (RuntimeException e) {
            log.error("Error updating goal: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete goal", description = "Delete goal by ID")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        try {
            goalService.deleteGoal(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting goal: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/progress")
    @Operation(summary = "Calculate goal progress", description = "Calculate progress for a goal with current amount")
    public ResponseEntity<GoalDTO> calculateProgress(@PathVariable Long id, @RequestParam Double currentAmount) {
        try {
            GoalDTO goalWithProgress = goalService.calculateProgress(id, currentAmount);
            return ResponseEntity.ok(goalWithProgress);
        } catch (RuntimeException e) {
            log.error("Error calculating progress: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}

// Made with Bob
