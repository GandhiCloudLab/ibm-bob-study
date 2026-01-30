package com.gan.wcare.investment.controller;

import com.gan.wcare.investment.dto.InvestmentDTO;
import com.gan.wcare.investment.dto.PortfolioSummaryDTO;
import com.gan.wcare.investment.service.InvestmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Investment REST Controller
 * Handles HTTP requests for investment management
 */
@RestController
@RequestMapping("/api/investments")
@Tag(name = "Investment Management", description = "APIs for managing investments and portfolios")
public class InvestmentController {

    private static final Logger logger = LoggerFactory.getLogger(InvestmentController.class);

    @Autowired
    private InvestmentService investmentService;

    /**
     * Get all investments
     */
    @GetMapping
    @Operation(summary = "Get all investments", description = "Retrieve all investments in the system")
    public ResponseEntity<List<InvestmentDTO>> getAllInvestments() {
        logger.info("GET /api/investments - Get all investments");
        List<InvestmentDTO> investments = investmentService.getAllInvestments();
        return ResponseEntity.ok(investments);
    }

    /**
     * Get investment by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get investment by ID", description = "Retrieve a specific investment by its ID")
    public ResponseEntity<InvestmentDTO> getInvestmentById(@PathVariable Long id) {
        logger.info("GET /api/investments/{} - Get investment by ID", id);
        InvestmentDTO investment = investmentService.getInvestmentById(id);
        return ResponseEntity.ok(investment);
    }

    /**
     * Get investments by customer ID
     */
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get investments by customer", description = "Retrieve all investments for a specific customer")
    public ResponseEntity<List<InvestmentDTO>> getInvestmentsByCustomerId(@PathVariable Long customerId) {
        logger.info("GET /api/investments/customer/{} - Get investments by customer ID", customerId);
        List<InvestmentDTO> investments = investmentService.getInvestmentsByCustomerId(customerId);
        return ResponseEntity.ok(investments);
    }

    /**
     * Get active investments by customer ID
     */
    @GetMapping("/customer/{customerId}/active")
    @Operation(summary = "Get active investments by customer", description = "Retrieve all active investments for a specific customer")
    public ResponseEntity<List<InvestmentDTO>> getActiveInvestmentsByCustomerId(@PathVariable Long customerId) {
        logger.info("GET /api/investments/customer/{}/active - Get active investments by customer ID", customerId);
        List<InvestmentDTO> investments = investmentService.getActiveInvestmentsByCustomerId(customerId);
        return ResponseEntity.ok(investments);
    }

    /**
     * Get investments by goal ID
     */
    @GetMapping("/goal/{goalId}")
    @Operation(summary = "Get investments by goal", description = "Retrieve all investments linked to a specific goal")
    public ResponseEntity<List<InvestmentDTO>> getInvestmentsByGoalId(@PathVariable Long goalId) {
        logger.info("GET /api/investments/goal/{} - Get investments by goal ID", goalId);
        List<InvestmentDTO> investments = investmentService.getInvestmentsByGoalId(goalId);
        return ResponseEntity.ok(investments);
    }

    /**
     * Get investments by type
     */
    @GetMapping("/type/{investmentType}")
    @Operation(summary = "Get investments by type", description = "Retrieve all investments of a specific type")
    public ResponseEntity<List<InvestmentDTO>> getInvestmentsByType(@PathVariable String investmentType) {
        logger.info("GET /api/investments/type/{} - Get investments by type", investmentType);
        List<InvestmentDTO> investments = investmentService.getInvestmentsByType(investmentType);
        return ResponseEntity.ok(investments);
    }

    /**
     * Get portfolio summary for a customer
     */
    @GetMapping("/customer/{customerId}/portfolio-summary")
    @Operation(summary = "Get portfolio summary", description = "Get aggregated portfolio summary for a customer")
    public ResponseEntity<PortfolioSummaryDTO> getPortfolioSummary(@PathVariable Long customerId) {
        logger.info("GET /api/investments/customer/{}/portfolio-summary - Get portfolio summary", customerId);
        PortfolioSummaryDTO summary = investmentService.getPortfolioSummary(customerId);
        return ResponseEntity.ok(summary);
    }

    /**
     * Create new investment
     */
    @PostMapping
    @Operation(summary = "Create investment", description = "Create a new investment")
    public ResponseEntity<InvestmentDTO> createInvestment(@Valid @RequestBody InvestmentDTO investmentDTO) {
        logger.info("POST /api/investments - Create new investment for customer: {}", investmentDTO.getCustomerId());
        InvestmentDTO createdInvestment = investmentService.createInvestment(investmentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdInvestment);
    }

    /**
     * Update investment
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update investment", description = "Update an existing investment")
    public ResponseEntity<InvestmentDTO> updateInvestment(
            @PathVariable Long id,
            @Valid @RequestBody InvestmentDTO investmentDTO) {
        logger.info("PUT /api/investments/{} - Update investment", id);
        InvestmentDTO updatedInvestment = investmentService.updateInvestment(id, investmentDTO);
        return ResponseEntity.ok(updatedInvestment);
    }

    /**
     * Update current price
     */
    @PatchMapping("/{id}/current-price")
    @Operation(summary = "Update current price", description = "Update the current market price of an investment")
    public ResponseEntity<InvestmentDTO> updateCurrentPrice(
            @PathVariable Long id,
            @RequestParam BigDecimal currentPrice) {
        logger.info("PATCH /api/investments/{}/current-price - Update current price to {}", id, currentPrice);
        InvestmentDTO updatedInvestment = investmentService.updateCurrentPrice(id, currentPrice);
        return ResponseEntity.ok(updatedInvestment);
    }

    /**
     * Link investment to goal
     */
    @PatchMapping("/{investmentId}/link-goal/{goalId}")
    @Operation(summary = "Link investment to goal", description = "Link an investment to a financial goal")
    public ResponseEntity<InvestmentDTO> linkInvestmentToGoal(
            @PathVariable Long investmentId,
            @PathVariable Long goalId) {
        logger.info("PATCH /api/investments/{}/link-goal/{} - Link investment to goal", investmentId, goalId);
        InvestmentDTO updatedInvestment = investmentService.linkInvestmentToGoal(investmentId, goalId);
        return ResponseEntity.ok(updatedInvestment);
    }

    /**
     * Delete investment
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete investment", description = "Delete an investment")
    public ResponseEntity<Void> deleteInvestment(@PathVariable Long id) {
        logger.info("DELETE /api/investments/{} - Delete investment", id);
        investmentService.deleteInvestment(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Exception handler for runtime exceptions
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        logger.error("Error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}

// Made with Bob
