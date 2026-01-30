package com.gan.wcare.investment.repository;

import com.gan.wcare.investment.model.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Investment Repository
 * Data access layer for Investment entity
 */
@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    /**
     * Find all investments for a customer
     */
    List<Investment> findByCustomerId(Long customerId);

    /**
     * Find all investments for a customer with specific status
     */
    List<Investment> findByCustomerIdAndStatus(Long customerId, String status);

    /**
     * Find all investments linked to a goal
     */
    List<Investment> findByGoalId(Long goalId);

    /**
     * Find all investments by type
     */
    List<Investment> findByInvestmentType(String investmentType);

    /**
     * Find all investments for a customer by type
     */
    List<Investment> findByCustomerIdAndInvestmentType(Long customerId, String investmentType);

    /**
     * Find investment by symbol for a customer
     */
    List<Investment> findByCustomerIdAndSymbol(Long customerId, String symbol);

    /**
     * Find all active investments for a customer
     */
    @Query("SELECT i FROM Investment i WHERE i.customerId = :customerId AND i.status = 'ACTIVE'")
    List<Investment> findActiveInvestmentsByCustomerId(@Param("customerId") Long customerId);

    /**
     * Count investments by customer
     */
    long countByCustomerId(Long customerId);

    /**
     * Count active investments by customer
     */
    long countByCustomerIdAndStatus(Long customerId, String status);

    /**
     * Check if investment exists for customer
     */
    boolean existsByCustomerIdAndSymbol(Long customerId, String symbol);
}

// Made with Bob
