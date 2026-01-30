package com.gan.wcare.goal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Goal Entity - Financial goals for customers
 */
@Entity
@Table(name = "wc_goal")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long customerId;  // Reference to Customer Service

    @Column(nullable = false)
    private Long wealthManagerId;  // Reference to Wealth Manager

    @Column(length = 80)
    private String goalReference;

    @Column(length = 200)
    private String goalDescription;

    private LocalDate startDate;

    private LocalDate targetDate;

    private Double targetAmount;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}

// Made with Bob
