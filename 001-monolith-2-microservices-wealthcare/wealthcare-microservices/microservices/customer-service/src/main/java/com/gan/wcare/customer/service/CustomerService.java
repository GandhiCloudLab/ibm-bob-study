package com.gan.wcare.customer.service;

import com.gan.wcare.customer.dto.CustomerDTO;
import com.gan.wcare.customer.model.Customer;
import com.gan.wcare.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Customer Service - Business logic for customer management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${services.user-service.url}")
    private String userServiceUrl;

    /**
     * Get all customers
     */
    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get customer by ID
     */
    @Transactional(readOnly = true)
    public Optional<CustomerDTO> getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(this::convertToDTO);
    }

    /**
     * Get customer by user ID
     */
    @Transactional(readOnly = true)
    public Optional<CustomerDTO> getCustomerByUserId(Long userId) {
        return customerRepository.findByUserId(userId)
                .map(this::convertToDTO);
    }

    /**
     * Get customers by wealth manager ID
     */
    @Transactional(readOnly = true)
    public List<CustomerDTO> getCustomersByWealthManagerId(Long wealthManagerId) {
        return customerRepository.findByWealthManagerId(wealthManagerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create new customer
     */
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        log.info("Creating new customer for user ID: {}", customerDTO.getUserId());

        // Check if customer already exists for this user
        if (customerRepository.existsByUserId(customerDTO.getUserId())) {
            throw new RuntimeException("Customer already exists for this user");
        }

        // Check if email already exists
        if (customerDTO.getEmailId() != null && 
            customerRepository.existsByEmailId(customerDTO.getEmailId())) {
            throw new RuntimeException("Email already exists");
        }

        Customer customer = convertToEntity(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);

        // Update user service with customer ID
        updateUserServiceWithCustomerId(savedCustomer.getUserId(), savedCustomer.getId());

        log.info("Customer created successfully with ID: {}", savedCustomer.getId());
        return convertToDTO(savedCustomer);
    }

    /**
     * Update customer
     */
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        log.info("Updating customer: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Update fields
        customer.setWealthManagerId(customerDTO.getWealthManagerId());
        customer.setFirstName(customerDTO.getFirstName());
        customer.setLastName(customerDTO.getLastName());
        customer.setGender(customerDTO.getGender());
        customer.setAge(customerDTO.getAge());
        customer.setAvgIncome(customerDTO.getAvgIncome());
        customer.setMarried(customerDTO.getMarried());
        
        // Spouse information
        customer.setSpouseFirstName(customerDTO.getSpouseFirstName());
        customer.setSpouseLastName(customerDTO.getSpouseLastName());
        customer.setSpouseGender(customerDTO.getSpouseGender());
        customer.setSpouseAge(customerDTO.getSpouseAge());
        customer.setSpouseAvgIncome(customerDTO.getSpouseAvgIncome());
        
        // Children information
        customer.setNoOfChildren(customerDTO.getNoOfChildren());
        customer.setChild1FirstName(customerDTO.getChild1FirstName());
        customer.setChild1LastName(customerDTO.getChild1LastName());
        customer.setChild1Gender(customerDTO.getChild1Gender());
        customer.setChild1Age(customerDTO.getChild1Age());
        customer.setChild2FirstName(customerDTO.getChild2FirstName());
        customer.setChild2LastName(customerDTO.getChild2LastName());
        customer.setChild2Gender(customerDTO.getChild2Gender());
        customer.setChild2Age(customerDTO.getChild2Age());
        
        // Contact information
        customer.setCity(customerDTO.getCity());
        customer.setPhone(customerDTO.getPhone());
        customer.setEmailId(customerDTO.getEmailId());
        customer.setCountry(customerDTO.getCountry());
        customer.setZipCode(customerDTO.getZipCode());
        customer.setStartDate(customerDTO.getStartDate());

        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Customer updated successfully: {}", updatedCustomer.getId());

        return convertToDTO(updatedCustomer);
    }

    /**
     * Delete customer
     */
    public void deleteCustomer(Long id) {
        log.info("Deleting customer: {}", id);
        
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        customerRepository.delete(customer);
        log.info("Customer deleted successfully: {}", id);
    }

    /**
     * Update user service with customer ID
     */
    private void updateUserServiceWithCustomerId(Long userId, Long customerId) {
        try {
            webClientBuilder.build()
                    .put()
                    .uri(userServiceUrl + "/api/users/" + userId + "/profile?role=CUSTOMER&profileId=" + customerId)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            log.info("Updated user service with customer ID: {}", customerId);
        } catch (Exception e) {
            log.error("Failed to update user service: {}", e.getMessage());
            // Don't fail the customer creation if user service update fails
        }
    }

    /**
     * Generate image URL based on gender
     */
    private String generateImageUrl(String gender, Long customerId) {
        String genderPrefix = "Male".equalsIgnoreCase(gender) ? "m" : "f";
        return "/images/" + genderPrefix + customerId + ".jpg";
    }

    /**
     * Convert Customer entity to DTO
     */
    private CustomerDTO convertToDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .userId(customer.getUserId())
                .wealthManagerId(customer.getWealthManagerId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .gender(customer.getGender())
                .age(customer.getAge())
                .avgIncome(customer.getAvgIncome())
                .married(customer.getMarried())
                .spouseFirstName(customer.getSpouseFirstName())
                .spouseLastName(customer.getSpouseLastName())
                .spouseGender(customer.getSpouseGender())
                .spouseAge(customer.getSpouseAge())
                .spouseAvgIncome(customer.getSpouseAvgIncome())
                .noOfChildren(customer.getNoOfChildren())
                .child1FirstName(customer.getChild1FirstName())
                .child1LastName(customer.getChild1LastName())
                .child1Gender(customer.getChild1Gender())
                .child1Age(customer.getChild1Age())
                .child2FirstName(customer.getChild2FirstName())
                .child2LastName(customer.getChild2LastName())
                .child2Gender(customer.getChild2Gender())
                .child2Age(customer.getChild2Age())
                .city(customer.getCity())
                .phone(customer.getPhone())
                .emailId(customer.getEmailId())
                .country(customer.getCountry())
                .zipCode(customer.getZipCode())
                .startDate(customer.getStartDate())
                .imageUrl(generateImageUrl(customer.getGender(), customer.getId()))
                .build();
    }

    /**
     * Convert DTO to Customer entity
     */
    private Customer convertToEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setUserId(dto.getUserId());
        customer.setWealthManagerId(dto.getWealthManagerId());
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setGender(dto.getGender());
        customer.setAge(dto.getAge());
        customer.setAvgIncome(dto.getAvgIncome());
        customer.setMarried(dto.getMarried());
        customer.setSpouseFirstName(dto.getSpouseFirstName());
        customer.setSpouseLastName(dto.getSpouseLastName());
        customer.setSpouseGender(dto.getSpouseGender());
        customer.setSpouseAge(dto.getSpouseAge());
        customer.setSpouseAvgIncome(dto.getSpouseAvgIncome());
        customer.setNoOfChildren(dto.getNoOfChildren());
        customer.setChild1FirstName(dto.getChild1FirstName());
        customer.setChild1LastName(dto.getChild1LastName());
        customer.setChild1Gender(dto.getChild1Gender());
        customer.setChild1Age(dto.getChild1Age());
        customer.setChild2FirstName(dto.getChild2FirstName());
        customer.setChild2LastName(dto.getChild2LastName());
        customer.setChild2Gender(dto.getChild2Gender());
        customer.setChild2Age(dto.getChild2Age());
        customer.setCity(dto.getCity());
        customer.setPhone(dto.getPhone());
        customer.setEmailId(dto.getEmailId());
        customer.setCountry(dto.getCountry());
        customer.setZipCode(dto.getZipCode());
        customer.setStartDate(dto.getStartDate());
        return customer;
    }
}

// Made with Bob
