package com.gan.wcare.customer.repository;

import com.gan.wcare.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Customer Repository
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByUserId(Long userId);

    List<Customer> findByWealthManagerId(Long wealthManagerId);

    boolean existsByUserId(Long userId);

    boolean existsByEmailId(String emailId);
}

// Made with Bob
