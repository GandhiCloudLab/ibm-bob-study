package com.gan.wcare.investment.service;

import com.gan.wcare.investment.dto.InvestmentDTO;
import com.gan.wcare.investment.dto.PortfolioSummaryDTO;
import com.gan.wcare.investment.model.Investment;
import com.gan.wcare.investment.repository.InvestmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Investment Service
 * Business logic for investment management
 */
@Service
@Transactional
public class InvestmentService {

    private static final Logger logger = LoggerFactory.getLogger(InvestmentService.class);

    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${services.customer-service.url}")
    private String customerServiceUrl;

    @Value("${services.goal-service.url}")
    private String goalServiceUrl;

    /**
     * Get all investments
     */
    public List<InvestmentDTO> getAllInvestments() {
        logger.info("Fetching all investments");
        return investmentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get investment by ID
     */
    public InvestmentDTO getInvestmentById(Long id) {
        logger.info("Fetching investment with id: {}", id);
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Investment not found with id: " + id));
        return convertToDTO(investment);
    }

    /**
     * Get investments by customer ID
     */
    public List<InvestmentDTO> getInvestmentsByCustomerId(Long customerId) {
        logger.info("Fetching investments for customer: {}", customerId);
        return investmentRepository.findByCustomerId(customerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get active investments by customer ID
     */
    public List<InvestmentDTO> getActiveInvestmentsByCustomerId(Long customerId) {
        logger.info("Fetching active investments for customer: {}", customerId);
        return investmentRepository.findActiveInvestmentsByCustomerId(customerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get investments by goal ID
     */
    public List<InvestmentDTO> getInvestmentsByGoalId(Long goalId) {
        logger.info("Fetching investments for goal: {}", goalId);
        return investmentRepository.findByGoalId(goalId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get investments by type
     */
    public List<InvestmentDTO> getInvestmentsByType(String investmentType) {
        logger.info("Fetching investments of type: {}", investmentType);
        return investmentRepository.findByInvestmentType(investmentType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create new investment
     */
    public InvestmentDTO createInvestment(InvestmentDTO investmentDTO) {
        logger.info("Creating new investment for customer: {}", investmentDTO.getCustomerId());

        // Validate customer exists
        validateCustomerExists(investmentDTO.getCustomerId());

        // Validate goal if provided
        if (investmentDTO.getGoalId() != null) {
            validateGoalExists(investmentDTO.getGoalId());
        }

        Investment investment = convertToEntity(investmentDTO);
        investment.setCreatedDate(LocalDate.now());
        investment.setStatus("ACTIVE");

        Investment savedInvestment = investmentRepository.save(investment);
        logger.info("Investment created successfully with id: {}", savedInvestment.getId());

        return convertToDTO(savedInvestment);
    }

    /**
     * Update investment
     */
    public InvestmentDTO updateInvestment(Long id, InvestmentDTO investmentDTO) {
        logger.info("Updating investment with id: {}", id);

        Investment existingInvestment = investmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Investment not found with id: " + id));

        // Update fields
        if (investmentDTO.getInvestmentName() != null) {
            existingInvestment.setInvestmentName(investmentDTO.getInvestmentName());
        }
        if (investmentDTO.getQuantity() != null) {
            existingInvestment.setQuantity(investmentDTO.getQuantity());
        }
        if (investmentDTO.getCurrentPrice() != null) {
            existingInvestment.setCurrentPrice(investmentDTO.getCurrentPrice());
        }
        if (investmentDTO.getGoalId() != null) {
            validateGoalExists(investmentDTO.getGoalId());
            existingInvestment.setGoalId(investmentDTO.getGoalId());
        }
        if (investmentDTO.getDescription() != null) {
            existingInvestment.setDescription(investmentDTO.getDescription());
        }
        if (investmentDTO.getStatus() != null) {
            existingInvestment.setStatus(investmentDTO.getStatus());
        }
        if (investmentDTO.getMaturityDate() != null) {
            existingInvestment.setMaturityDate(investmentDTO.getMaturityDate());
        }

        existingInvestment.setLastUpdatedDate(LocalDate.now());

        Investment updatedInvestment = investmentRepository.save(existingInvestment);
        logger.info("Investment updated successfully with id: {}", updatedInvestment.getId());

        return convertToDTO(updatedInvestment);
    }

    /**
     * Update current price for an investment
     */
    public InvestmentDTO updateCurrentPrice(Long id, BigDecimal currentPrice) {
        logger.info("Updating current price for investment: {}", id);

        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Investment not found with id: " + id));

        investment.setCurrentPrice(currentPrice);
        investment.setLastUpdatedDate(LocalDate.now());

        Investment updatedInvestment = investmentRepository.save(investment);
        logger.info("Current price updated successfully for investment: {}", id);

        return convertToDTO(updatedInvestment);
    }

    /**
     * Delete investment
     */
    public void deleteInvestment(Long id) {
        logger.info("Deleting investment with id: {}", id);

        if (!investmentRepository.existsById(id)) {
            throw new RuntimeException("Investment not found with id: " + id);
        }

        investmentRepository.deleteById(id);
        logger.info("Investment deleted successfully with id: {}", id);
    }

    /**
     * Get portfolio summary for a customer
     */
    public PortfolioSummaryDTO getPortfolioSummary(Long customerId) {
        logger.info("Generating portfolio summary for customer: {}", customerId);

        List<Investment> investments = investmentRepository.findActiveInvestmentsByCustomerId(customerId);

        PortfolioSummaryDTO summary = new PortfolioSummaryDTO(customerId);
        summary.setTotalInvestments(investments.size());

        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal currentValue = BigDecimal.ZERO;

        for (Investment investment : investments) {
            BigDecimal investmentAmount = investment.getTotalInvestment();
            BigDecimal currentVal = investment.getCurrentValue();

            totalInvested = totalInvested.add(investmentAmount);
            currentValue = currentValue.add(currentVal);

            summary.addInvestmentByType(investment.getInvestmentType(), currentVal);
        }

        summary.setTotalInvested(totalInvested);
        summary.setCurrentValue(currentValue);
        summary.setTotalGainLoss(currentValue.subtract(totalInvested));

        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal gainLossPercentage = summary.getTotalGainLoss()
                    .divide(totalInvested, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"));
            summary.setTotalGainLossPercentage(gainLossPercentage);
        }

        logger.info("Portfolio summary generated for customer: {}", customerId);
        return summary;
    }

    /**
     * Link investment to goal
     */
    public InvestmentDTO linkInvestmentToGoal(Long investmentId, Long goalId) {
        logger.info("Linking investment {} to goal {}", investmentId, goalId);

        Investment investment = investmentRepository.findById(investmentId)
                .orElseThrow(() -> new RuntimeException("Investment not found with id: " + investmentId));

        validateGoalExists(goalId);

        investment.setGoalId(goalId);
        investment.setLastUpdatedDate(LocalDate.now());

        Investment updatedInvestment = investmentRepository.save(investment);
        logger.info("Investment linked to goal successfully");

        return convertToDTO(updatedInvestment);
    }

    /**
     * Validate customer exists via Customer Service
     */
    private void validateCustomerExists(Long customerId) {
        try {
            logger.debug("Validating customer exists: {}", customerId);
            webClientBuilder.build()
                    .get()
                    .uri(customerServiceUrl + "/api/customers/" + customerId)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
            logger.debug("Customer validation successful: {}", customerId);
        } catch (Exception e) {
            logger.error("Customer validation failed for id: {}", customerId, e);
            throw new RuntimeException("Customer not found with id: " + customerId);
        }
    }

    /**
     * Validate goal exists via Goal Service
     */
    private void validateGoalExists(Long goalId) {
        try {
            logger.debug("Validating goal exists: {}", goalId);
            webClientBuilder.build()
                    .get()
                    .uri(goalServiceUrl + "/api/goals/" + goalId)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
            logger.debug("Goal validation successful: {}", goalId);
        } catch (Exception e) {
            logger.error("Goal validation failed for id: {}", goalId, e);
            throw new RuntimeException("Goal not found with id: " + goalId);
        }
    }

    /**
     * Convert Investment entity to DTO
     */
    private InvestmentDTO convertToDTO(Investment investment) {
        InvestmentDTO dto = new InvestmentDTO();
        dto.setId(investment.getId());
        dto.setCustomerId(investment.getCustomerId());
        dto.setGoalId(investment.getGoalId());
        dto.setInvestmentName(investment.getInvestmentName());
        dto.setInvestmentType(investment.getInvestmentType());
        dto.setSymbol(investment.getSymbol());
        dto.setQuantity(investment.getQuantity());
        dto.setPurchasePrice(investment.getPurchasePrice());
        dto.setCurrentPrice(investment.getCurrentPrice());
        dto.setPurchaseDate(investment.getPurchaseDate());
        dto.setMaturityDate(investment.getMaturityDate());
        dto.setDescription(investment.getDescription());
        dto.setStatus(investment.getStatus());
        dto.setCreatedDate(investment.getCreatedDate());
        dto.setLastUpdatedDate(investment.getLastUpdatedDate());

        // Set calculated fields
        dto.setTotalInvestment(investment.getTotalInvestment());
        dto.setCurrentValue(investment.getCurrentValue());
        dto.setGainLoss(investment.getGainLoss());
        dto.setGainLossPercentage(investment.getGainLossPercentage());

        return dto;
    }

    /**
     * Convert DTO to Investment entity
     */
    private Investment convertToEntity(InvestmentDTO dto) {
        Investment investment = new Investment();
        investment.setCustomerId(dto.getCustomerId());
        investment.setGoalId(dto.getGoalId());
        investment.setInvestmentName(dto.getInvestmentName());
        investment.setInvestmentType(dto.getInvestmentType());
        investment.setSymbol(dto.getSymbol());
        investment.setQuantity(dto.getQuantity());
        investment.setPurchasePrice(dto.getPurchasePrice());
        investment.setCurrentPrice(dto.getCurrentPrice());
        investment.setPurchaseDate(dto.getPurchaseDate());
        investment.setMaturityDate(dto.getMaturityDate());
        investment.setDescription(dto.getDescription());
        investment.setStatus(dto.getStatus());
        return investment;
    }
}

// Made with Bob
