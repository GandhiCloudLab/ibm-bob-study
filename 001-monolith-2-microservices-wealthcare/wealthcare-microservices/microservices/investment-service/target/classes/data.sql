-- Sample Investment Data for Wealthcare Application

-- Customer 1 Investments (Rajesh Kumar)
INSERT INTO wc_investment (customer_id, goal_id, investment_name, investment_type, symbol, quantity, purchase_price, current_price, purchase_date, maturity_date, description, status, created_date) VALUES
(1, 1, 'HDFC Bank Shares', 'STOCKS', 'HDFCBANK', 100, 1500.00, 1650.00, '2023-01-15', NULL, 'Blue chip banking stock', 'ACTIVE', '2023-01-15'),
(1, 1, 'Reliance Industries', 'STOCKS', 'RELIANCE', 50, 2400.00, 2550.00, '2023-02-20', NULL, 'Diversified conglomerate', 'ACTIVE', '2023-02-20'),
(1, 1, 'SBI Bluechip Fund', 'MUTUAL_FUNDS', 'SBI-BC', 500, 65.00, 72.50, '2023-03-10', NULL, 'Large cap mutual fund', 'ACTIVE', '2023-03-10'),
(1, 2, 'PPF Account', 'BONDS', 'PPF', 1, 150000.00, 165000.00, '2022-04-01', '2037-04-01', 'Public Provident Fund for retirement', 'ACTIVE', '2022-04-01');

-- Customer 2 Investments (Priya Sharma)
INSERT INTO wc_investment (customer_id, goal_id, investment_name, investment_type, symbol, quantity, purchase_price, current_price, purchase_date, maturity_date, description, status, created_date) VALUES
(2, 3, 'Infosys Shares', 'STOCKS', 'INFY', 75, 1400.00, 1520.00, '2023-01-25', NULL, 'IT sector leader', 'ACTIVE', '2023-01-25'),
(2, 3, 'ICICI Prudential Fund', 'MUTUAL_FUNDS', 'ICICI-PR', 300, 85.00, 92.00, '2023-04-15', NULL, 'Balanced mutual fund', 'ACTIVE', '2023-04-15'),
(2, 4, 'Gold ETF', 'GOLD', 'GOLDBEES', 200, 55.00, 58.50, '2023-05-20', NULL, 'Gold exchange traded fund', 'ACTIVE', '2023-05-20'),
(2, NULL, 'Fixed Deposit', 'BONDS', 'FD-HDFC', 1, 500000.00, 525000.00, '2023-06-01', '2026-06-01', '3-year fixed deposit at 7% p.a.', 'ACTIVE', '2023-06-01');

-- Customer 3 Investments (Amit Patel)
INSERT INTO wc_investment (customer_id, goal_id, investment_name, investment_type, symbol, quantity, purchase_price, current_price, purchase_date, maturity_date, description, status, created_date) VALUES
(3, 5, 'TCS Shares', 'STOCKS', 'TCS', 60, 3200.00, 3450.00, '2023-02-10', NULL, 'Leading IT services company', 'ACTIVE', '2023-02-10'),
(3, 5, 'Nifty 50 ETF', 'ETF', 'NIFTYBEES', 400, 180.00, 195.00, '2023-03-15', NULL, 'Index fund tracking Nifty 50', 'ACTIVE', '2023-03-15'),
(3, 6, 'Real Estate Plot', 'REAL_ESTATE', 'PLOT-MUM', 1, 5000000.00, 5500000.00, '2022-08-01', NULL, 'Residential plot in Mumbai suburbs', 'ACTIVE', '2022-08-01');

-- Customer 4 Investments (Sneha Reddy)
INSERT INTO wc_investment (customer_id, goal_id, investment_name, investment_type, symbol, quantity, purchase_price, current_price, purchase_date, maturity_date, description, status, created_date) VALUES
(4, 7, 'Axis Bluechip Fund', 'MUTUAL_FUNDS', 'AXIS-BC', 600, 45.00, 51.00, '2023-01-20', NULL, 'Large cap equity fund', 'ACTIVE', '2023-01-20'),
(4, 7, 'HDFC Top 100 Fund', 'MUTUAL_FUNDS', 'HDFC-T100', 400, 75.00, 82.00, '2023-02-25', NULL, 'Top 100 companies fund', 'ACTIVE', '2023-02-25'),
(4, 8, 'National Savings Certificate', 'BONDS', 'NSC', 1, 100000.00, 110000.00, '2022-12-01', '2027-12-01', '5-year NSC for tax saving', 'ACTIVE', '2022-12-01');

-- Customer 5 Investments (Vikram Singh)
INSERT INTO wc_investment (customer_id, goal_id, investment_name, investment_type, symbol, quantity, purchase_price, current_price, purchase_date, maturity_date, description, status, created_date) VALUES
(5, 9, 'Wipro Shares', 'STOCKS', 'WIPRO', 150, 420.00, 445.00, '2023-03-05', NULL, 'IT services company', 'ACTIVE', '2023-03-05'),
(5, 9, 'Mirae Asset Emerging Fund', 'MUTUAL_FUNDS', 'MIRAE-EM', 500, 55.00, 62.00, '2023-04-10', NULL, 'Emerging bluechip fund', 'ACTIVE', '2023-04-10'),
(5, 10, 'Bitcoin', 'CRYPTO', 'BTC', 0.5, 3500000.00, 3800000.00, '2023-05-15', NULL, 'Cryptocurrency investment', 'ACTIVE', '2023-05-15'),
(5, NULL, 'Corporate Bonds', 'BONDS', 'CORP-BOND', 10, 100000.00, 105000.00, '2023-06-20', '2028-06-20', 'AAA rated corporate bonds', 'ACTIVE', '2023-06-20');

-- Customer 6 Investments (Ananya Iyer)
INSERT INTO wc_investment (customer_id, goal_id, investment_name, investment_type, symbol, quantity, purchase_price, current_price, purchase_date, maturity_date, description, status, created_date) VALUES
(6, 11, 'Asian Paints Shares', 'STOCKS', 'ASIANPAINT', 40, 2800.00, 3050.00, '2023-02-15', NULL, 'Leading paint manufacturer', 'ACTIVE', '2023-02-15'),
(6, 11, 'SBI Small Cap Fund', 'MUTUAL_FUNDS', 'SBI-SC', 350, 95.00, 108.00, '2023-03-20', NULL, 'Small cap equity fund', 'ACTIVE', '2023-03-20'),
(6, 12, 'Sovereign Gold Bonds', 'GOLD', 'SGB-2023', 50, 5500.00, 5850.00, '2023-04-01', '2031-04-01', 'Government gold bonds', 'ACTIVE', '2023-04-01'),
(6, NULL, 'Emergency Fund FD', 'BONDS', 'FD-ICICI', 1, 300000.00, 310000.00, '2023-05-01', '2024-05-01', 'Liquid emergency fund', 'ACTIVE', '2023-05-01');

-- Some sold/matured investments for history
INSERT INTO wc_investment (customer_id, goal_id, investment_name, investment_type, symbol, quantity, purchase_price, current_price, purchase_date, maturity_date, description, status, created_date, last_updated_date) VALUES
(1, NULL, 'Tata Motors Shares', 'STOCKS', 'TATAMOTORS', 200, 450.00, 520.00, '2022-06-10', NULL, 'Automobile sector - Sold for profit', 'SOLD', '2022-06-10', '2023-03-15'),
(2, NULL, 'Short Term FD', 'BONDS', 'FD-SBI', 1, 200000.00, 210000.00, '2022-07-01', '2023-07-01', 'Matured fixed deposit', 'MATURED', '2022-07-01', '2023-07-01'),
(3, NULL, 'Kotak Equity Fund', 'MUTUAL_FUNDS', 'KOTAK-EQ', 250, 60.00, 68.00, '2022-08-15', NULL, 'Redeemed for home purchase', 'SOLD', '2022-08-15', '2023-04-20');

-- Made with Bob
