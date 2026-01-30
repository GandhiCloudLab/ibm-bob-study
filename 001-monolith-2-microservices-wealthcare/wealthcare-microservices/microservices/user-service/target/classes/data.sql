-- Initial user data for Wealthcare User Service
-- All passwords are "password" for demo purposes
-- BCrypt hash for "password": $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG

-- Business Managers
INSERT INTO wc_users (username, password, email_id, role, active, business_manager_id, wealth_manager_id, customer_id, created_at, updated_at)
VALUES ('harry', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'harry@wcare.com', 'BUSINESS_MANAGER', true, 1, null, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO wc_users (username, password, email_id, role, active, business_manager_id, wealth_manager_id, customer_id, created_at, updated_at)
VALUES ('charlie', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'charlie@wcare.com', 'BUSINESS_MANAGER', true, 2, null, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO wc_users (username, password, email_id, role, active, business_manager_id, wealth_manager_id, customer_id, created_at, updated_at)
VALUES ('oliver', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'oliver@wcare.com', 'BUSINESS_MANAGER', true, 3, null, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Wealth Managers
INSERT INTO wc_users (username, password, email_id, role, active, business_manager_id, wealth_manager_id, customer_id, created_at, updated_at)
VALUES ('jerald', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'jerald@wcare.com', 'WEALTH_MANAGER', true, null, 1, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO wc_users (username, password, email_id, role, active, business_manager_id, wealth_manager_id, customer_id, created_at, updated_at)
VALUES ('charlotte', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'charlotte@wcare.com', 'WEALTH_MANAGER', true, null, 2, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO wc_users (username, password, email_id, role, active, business_manager_id, wealth_manager_id, customer_id, created_at, updated_at)
VALUES ('mia', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'mia@wcare.com', 'WEALTH_MANAGER', true, null, 3, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO wc_users (username, password, email_id, role, active, business_manager_id, wealth_manager_id, customer_id, created_at, updated_at)
VALUES ('tod', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'tod@wcare.com', 'WEALTH_MANAGER', true, null, 4, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO wc_users (username, password, email_id, role, active, business_manager_id, wealth_manager_id, customer_id, created_at, updated_at)
VALUES ('sophia', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'sophia@wcare.com', 'WEALTH_MANAGER', true, null, 5, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO wc_users (username, password, email_id, role, active, business_manager_id, wealth_manager_id, customer_id, created_at, updated_at)
VALUES ('sam', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'sam@wcare.com', 'WEALTH_MANAGER', true, null, 6, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Customers
INSERT INTO wc_users (username, password, email_id, role, active, business_manager_id, wealth_manager_id, customer_id, created_at, updated_at)
VALUES ('william', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'william@wcare.com', 'CUSTOMER', true, null, null, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO wc_users (username, password, email_id, role, active, business_manager_id, wealth_manager_id, customer_id, created_at, updated_at)
VALUES ('sandy', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'sandy@wcare.com', 'CUSTOMER', true, null, null, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO wc_users (username, password, email_id, role, active, business_manager_id, wealth_manager_id, customer_id, created_at, updated_at)
VALUES ('david', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'david@wcare.com', 'CUSTOMER', true, null, null, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO wc_users (username, password, email_id, role, active, business_manager_id, wealth_manager_id, customer_id, created_at, updated_at)
VALUES ('richard', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'richard@wcare.com', 'CUSTOMER', true, null, null, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO wc_users (username, password, email_id, role, active, business_manager_id, wealth_manager_id, customer_id, created_at, updated_at)
VALUES ('emma', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'emma@wcare.com', 'CUSTOMER', true, null, null, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO wc_users (username, password, email_id, role, active, business_manager_id, wealth_manager_id, customer_id, created_at, updated_at)
VALUES ('tom', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'tom@wcare.com', 'CUSTOMER', true, null, null, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Made with Bob
