package com.gan.wcare.goal.repository;

import com.gan.wcare.goal.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Goal Repository
 */
@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByCustomerId(Long customerId);

    List<Goal> findByWealthManagerId(Long wealthManagerId);

    List<Goal> findByCustomerIdAndWealthManagerId(Long customerId, Long wealthManagerId);
}

// Made with Bob
