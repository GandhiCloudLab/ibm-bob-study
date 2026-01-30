package com.gan.wcare.investment.dto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Portfolio Summary Data Transfer Object
 * Provides aggregated portfolio information
 */
public class PortfolioSummaryDTO {

    private Long customerId;
    private Integer totalInvestments;
    private BigDecimal totalInvested;
    private BigDecimal currentValue;
    private BigDecimal totalGainLoss;
    private BigDecimal totalGainLossPercentage;
    private Map<String, BigDecimal> investmentsByType;
    private Map<String, Integer> countByType;

    // Constructors
    public PortfolioSummaryDTO() {
        this.investmentsByType = new HashMap<>();
        this.countByType = new HashMap<>();
        this.totalInvested = BigDecimal.ZERO;
        this.currentValue = BigDecimal.ZERO;
        this.totalGainLoss = BigDecimal.ZERO;
        this.totalGainLossPercentage = BigDecimal.ZERO;
        this.totalInvestments = 0;
    }

    public PortfolioSummaryDTO(Long customerId) {
        this();
        this.customerId = customerId;
    }

    // Getters and Setters
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Integer getTotalInvestments() {
        return totalInvestments;
    }

    public void setTotalInvestments(Integer totalInvestments) {
        this.totalInvestments = totalInvestments;
    }

    public BigDecimal getTotalInvested() {
        return totalInvested;
    }

    public void setTotalInvested(BigDecimal totalInvested) {
        this.totalInvested = totalInvested;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }

    public BigDecimal getTotalGainLoss() {
        return totalGainLoss;
    }

    public void setTotalGainLoss(BigDecimal totalGainLoss) {
        this.totalGainLoss = totalGainLoss;
    }

    public BigDecimal getTotalGainLossPercentage() {
        return totalGainLossPercentage;
    }

    public void setTotalGainLossPercentage(BigDecimal totalGainLossPercentage) {
        this.totalGainLossPercentage = totalGainLossPercentage;
    }

    public Map<String, BigDecimal> getInvestmentsByType() {
        return investmentsByType;
    }

    public void setInvestmentsByType(Map<String, BigDecimal> investmentsByType) {
        this.investmentsByType = investmentsByType;
    }

    public Map<String, Integer> getCountByType() {
        return countByType;
    }

    public void setCountByType(Map<String, Integer> countByType) {
        this.countByType = countByType;
    }

    // Helper methods
    public void addInvestmentByType(String type, BigDecimal amount) {
        investmentsByType.merge(type, amount, BigDecimal::add);
        countByType.merge(type, 1, Integer::sum);
    }
}

// Made with Bob
