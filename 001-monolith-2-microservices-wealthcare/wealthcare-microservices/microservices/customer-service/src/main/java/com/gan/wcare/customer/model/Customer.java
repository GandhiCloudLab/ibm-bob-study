package com.gan.wcare.customer.model;

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
 * Customer Entity - Customer profile and family information
 */
@Entity
@Table(name = "wc_customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;  // Reference to User Service

    @Column(name = "wealth_manager_id", nullable = false)
    private Long wealthManagerId;  // Reference to Wealth Manager

    // Personal Information
    @Column(name = "first_name", nullable = false, length = 40)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 40)
    private String lastName;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "age")
    private Integer age;

    @Column(name = "avg_income")
    private Double avgIncome;

    // Marital Status
    @Column(name = "married")
    private Boolean married = false;

    // Spouse Information
    @Column(name = "spouse_first_name")
    private String spouseFirstName;
    
    @Column(name = "spouse_last_name")
    private String spouseLastName;
    
    @Column(name = "spouse_gender")
    private String spouseGender;
    
    @Column(name = "spouse_age")
    private Integer spouseAge;
    
    @Column(name = "spouse_avg_income")
    private Double spouseAvgIncome;

    // Children Information
    @Column(name = "no_of_children")
    private Integer noOfChildren = 0;

    // Child 1
    @Column(name = "child1_first_name")
    private String child1FirstName;
    
    @Column(name = "child1_last_name")
    private String child1LastName;
    
    @Column(name = "child1_gender")
    private String child1Gender;
    
    @Column(name = "child1_age")
    private Integer child1Age;

    // Child 2
    @Column(name = "child2_first_name")
    private String child2FirstName;
    
    @Column(name = "child2_last_name")
    private String child2LastName;
    
    @Column(name = "child2_gender")
    private String child2Gender;
    
    @Column(name = "child2_age")
    private Integer child2Age;

    // Contact Information
    @Column(name = "city", length = 40)
    private String city;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "email_id", length = 100)
    private String emailId;

    @Column(name = "country", length = 20)
    private String country;

    @Column(name = "zip_code", length = 20)
    private String zipCode;

    @Column(name = "start_date")
    private LocalDate startDate;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}

// Made with Bob
