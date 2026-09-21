-- Test users for approval workflow testing
-- Password for all: Password123!

IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'fm@onemore.com')
INSERT INTO users (employee_code, full_name, email, password_hash, job_level, function_name, business, is_active, created_at, updated_at)
VALUES ('EMP-FM01', 'Fiona Manager', 'fm@onemore.com', '$2b$12$nT6jGfO3QIPL.OedfxiTpO3aX/bptfhYyNQElkIMzHaeMDjBkCIaO', 'FUNCTION_MANAGER', 'IT', 'OneMore Group', 1, SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET());

IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'hrbp@onemore.com')
INSERT INTO users (employee_code, full_name, email, password_hash, job_level, function_name, business, is_active, created_at, updated_at)
VALUES ('EMP-HR01', 'Helen Roberts', 'hrbp@onemore.com', '$2b$12$nT6jGfO3QIPL.OedfxiTpO3aX/bptfhYyNQElkIMzHaeMDjBkCIaO', 'STAFF', 'HR', 'OneMore Group', 1, SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET());

IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'finance@onemore.com')
INSERT INTO users (employee_code, full_name, email, password_hash, job_level, function_name, business, is_active, created_at, updated_at)
VALUES ('EMP-FN01', 'Frank Nolan', 'finance@onemore.com', '$2b$12$nT6jGfO3QIPL.OedfxiTpO3aX/bptfhYyNQElkIMzHaeMDjBkCIaO', 'STAFF', 'Finance', 'OneMore Group', 1, SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET());

IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'bizops@onemore.com')
INSERT INTO users (employee_code, full_name, email, password_hash, job_level, function_name, business, is_active, created_at, updated_at)
VALUES ('EMP-BO01', 'Bella Ops', 'bizops@onemore.com', '$2b$12$nT6jGfO3QIPL.OedfxiTpO3aX/bptfhYyNQElkIMzHaeMDjBkCIaO', 'STAFF', 'BizOps', 'OneMore Group', 1, SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET());

IF NOT EXISTS (SELECT 1 FROM user_roles ur JOIN users u ON ur.user_id = u.id WHERE u.email = 'fm@onemore.com' AND ur.role = 'ROLE_FUNCTION_MANAGER')
INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_FUNCTION_MANAGER' FROM users WHERE email = 'fm@onemore.com';

IF NOT EXISTS (SELECT 1 FROM user_roles ur JOIN users u ON ur.user_id = u.id WHERE u.email = 'hrbp@onemore.com' AND ur.role = 'ROLE_HRBP')
INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_HRBP' FROM users WHERE email = 'hrbp@onemore.com';

IF NOT EXISTS (SELECT 1 FROM user_roles ur JOIN users u ON ur.user_id = u.id WHERE u.email = 'finance@onemore.com' AND ur.role = 'ROLE_FINANCE')
INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_FINANCE' FROM users WHERE email = 'finance@onemore.com';

IF NOT EXISTS (SELECT 1 FROM user_roles ur JOIN users u ON ur.user_id = u.id WHERE u.email = 'bizops@onemore.com' AND ur.role = 'ROLE_BIZOPS')
INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_BIZOPS' FROM users WHERE email = 'bizops@onemore.com';