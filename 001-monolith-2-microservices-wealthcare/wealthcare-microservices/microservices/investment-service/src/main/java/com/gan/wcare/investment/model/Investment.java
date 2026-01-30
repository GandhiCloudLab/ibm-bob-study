package com.gan.wcare.investment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Investment Entity
 * Represents an investment in a customer's portfolio
 */
@Entity
@Table(name = "wc_investment")
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Customer ID is required")
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "goal_id")
    private Long goalId;

    @NotBlank(message = "Investment name is required")
    @Size(max = 100, message = "Investment name must not exceed 100 characters")
    @Column(name = "investment_name", nullable = false, length = 100)
    private String investmentName;

    @NotBlank(message = "Investment type is required")
    @Column(name = "investment_type", nullable = false, length = 50)
    private String investmentType; // STOCKS, BONDS, MUTUAL_FUNDS, ETF, REAL_ESTATE, GOLD, CRYPTO, OTHER

    @NotBlank(message = "Symbol/Ticker is required")
    @Size(max = 20, message = "Symbol must not exceed 20 characters")
    @Column(name = "symbol", nullable = false, length = 20)
    private String symbol;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be greater than 0")
    @Column(name = "quantity", nullable = false, precision = 15, scale = 4)
    private BigDecimal quantity;

    @NotNull(message = "Purchase price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Purchase price must be greater than 0")
    @Column(name = "purchase_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "current_price", precision = 15, scale = 2)
    private BigDecimal currentPrice;

    @NotNull(message = "Purchase date is required")
    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @Column(name = "maturity_date")
    private LocalDate maturityDate;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "status", length = 20)
    private String status = "ACTIVE"; // ACTIVE, SOLD, MATURED

    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;

    @Column(name = "last_updated_date")
    private LocalDate lastUpdatedDate;

    // Constructors
    public Investment() {
        this.createdDate = LocalDate.now();
        this.status = "ACTIVE";
    }

    public Investment(Long customerId, String investmentName, String investmentType, 
                     String symbol, BigDecimal quantity, BigDecimal purchasePrice, 
                     LocalDate purchaseDate) {
        this();
        this.customerId = customerId;
        this.investmentName = investmentName;
        this.investmentType = investmentType;
        this.symbol = symbol;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
        this.purchaseDate = purchaseDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getGoalId() {
        return goalId;
    }

    public void setGoalId(Long goalId) {
        this.goalId = goalId;
    }

    public String getInvestmentName() {
        return investmentName;
    }

    public void setInvestmentName(String investmentName) {
        this.investmentName = investmentName;
    }

    public String getInvestmentType() {
        return investmentType;
    }

    public void setInvestmentType(String investmentType) {
        this.investmentType = investmentType;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(LocalDate maturityDate) {
        this.maturityDate = maturityDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(LocalDate lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    // Business methods
    public BigDecimal getTotalInvestment() {
        return purchasePrice.multiply(quantity);
    }

    public BigDecimal getCurrentValue() {
        if (currentPrice != null) {
            return currentPrice.multiply(quantity);
        }
        return getTotalInvestment();
    }

    public BigDecimal getGainLoss() {
        return getCurrentValue().subtract(getTotalInvestment());
    }

    public BigDecimal getGainLossPercentage() {
        BigDecimal totalInvestment = getTotalInvestment();
        if (totalInvestment.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return getGainLoss()
                .divide(totalInvestment, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    @PreUpdate
    public void preUpdate() {
        this.lastUpdatedDate = LocalDate.now();
    }

    @Override
    public String toString() {
        return "Investment{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", investmentName='" + investmentName + '\'' +
                ", symbol='" + symbol + '\'' +
                ", quantity=" + quantity +
                ", purchasePrice=" + purchasePrice +
                ", currentPrice=" + currentPrice +
                ", status='" + status + '\'' +
                '}';
    }
}

// Made with Bob
