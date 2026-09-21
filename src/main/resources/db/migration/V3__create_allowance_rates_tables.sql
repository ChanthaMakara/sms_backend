CREATE TABLE meal_rates (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    location_tier VARCHAR(20) NOT NULL,
    job_level VARCHAR(30) NOT NULL,
    breakfast_amount DECIMAL(10,2) NOT NULL,
    lunch_amount DECIMAL(10,2) NOT NULL,
    dinner_amount DECIMAL(10,2) NOT NULL,
    created_at DATETIMEOFFSET(6) NOT NULL DEFAULT SYSDATETIMEOFFSET(),
    updated_at DATETIMEOFFSET(6) NOT NULL DEFAULT SYSDATETIMEOFFSET(),
    CONSTRAINT UQ_meal_rate_tier_level UNIQUE (location_tier, job_level)
);

CREATE TABLE accommodation_rates (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    location_tier VARCHAR(20) NOT NULL,
    job_level VARCHAR(30) NOT NULL,
    amount_per_night DECIMAL(10,2) NOT NULL,
    created_at DATETIMEOFFSET(6) NOT NULL DEFAULT SYSDATETIMEOFFSET(),
    updated_at DATETIMEOFFSET(6) NOT NULL DEFAULT SYSDATETIMEOFFSET(),
    CONSTRAINT UQ_accommodation_rate_tier_level UNIQUE (location_tier, job_level)
);

-- Placeholder seed data (REPLACE WITH REAL COMPANY RATES LATER)
INSERT INTO meal_rates (location_tier, job_level, breakfast_amount, lunch_amount, dinner_amount) VALUES
('TIER_1', 'EXECUTIVE', 10.00, 15.00, 20.00),
('TIER_1', 'FUNCTION_MANAGER', 8.00, 12.00, 16.00),
('TIER_1', 'SUB_FUNCTION_MANAGER', 7.00, 10.00, 14.00),
('TIER_1', 'STAFF', 5.00, 8.00, 10.00),
('TIER_1', 'DRIVER', 5.00, 8.00, 10.00),

('TIER_2', 'EXECUTIVE', 8.00, 12.00, 16.00),
('TIER_2', 'FUNCTION_MANAGER', 6.00, 10.00, 14.00),
('TIER_2', 'SUB_FUNCTION_MANAGER', 5.50, 9.00, 12.00),
('TIER_2', 'STAFF', 4.00, 6.00, 8.00),
('TIER_2', 'DRIVER', 4.00, 6.00, 8.00),

('TIER_3', 'EXECUTIVE', 6.00, 10.00, 12.00),
('TIER_3', 'FUNCTION_MANAGER', 5.00, 8.00, 10.00),
('TIER_3', 'SUB_FUNCTION_MANAGER', 4.50, 7.00, 9.00),
('TIER_3', 'STAFF', 3.00, 5.00, 7.00),
('TIER_3', 'DRIVER', 3.00, 5.00, 7.00);

-- Placeholder seed data (REPLACE WITH REAL COMPANY RATES LATER)
INSERT INTO accommodation_rates (location_tier, job_level, amount_per_night) VALUES
('TIER_1', 'EXECUTIVE', 100.00),
('TIER_1', 'FUNCTION_MANAGER', 80.00),
('TIER_1', 'SUB_FUNCTION_MANAGER', 70.00),
('TIER_1', 'STAFF', 50.00),
('TIER_1', 'DRIVER', 50.00),

('TIER_2', 'EXECUTIVE', 80.00),
('TIER_2', 'FUNCTION_MANAGER', 65.00),
('TIER_2', 'SUB_FUNCTION_MANAGER', 55.00),
('TIER_2', 'STAFF', 40.00),
('TIER_2', 'DRIVER', 40.00),

('TIER_3', 'EXECUTIVE', 60.00),
('TIER_3', 'FUNCTION_MANAGER', 50.00),
('TIER_3', 'SUB_FUNCTION_MANAGER', 45.00),
('TIER_3', 'STAFF', 30.00),
('TIER_3', 'DRIVER', 30.00);