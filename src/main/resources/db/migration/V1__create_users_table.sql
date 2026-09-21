-- V1__create_users_table.sql

CREATE TABLE users (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    employee_code   VARCHAR(50)  NOT NULL UNIQUE,
    full_name       NVARCHAR(150) NOT NULL,
    email           VARCHAR(150) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    job_level       VARCHAR(50)  NOT NULL,          -- EXECUTIVE, FUNCTION_MANAGER, etc.
    function_name   NVARCHAR(100),
    business        NVARCHAR(100),
    is_active       BIT          NOT NULL DEFAULT 1,
    created_at      DATETIMEOFFSET(6) NOT NULL DEFAULT SYSUTCDATETIME(),
    updated_at      DATETIMEOFFSET(6) NOT NULL DEFAULT SYSUTCDATETIME()
);

CREATE TABLE user_roles (
    user_id     BIGINT       NOT NULL,
    role        VARCHAR(50)  NOT NULL,              -- ROLE_EMPLOYEE, ROLE_FUNCTION_MANAGER, ROLE_HRBP, ROLE_FINANCE, ROLE_BIZOPS, ROLE_EXECUTIVE, ROLE_ADMIN
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Index for faster login
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_employee_code ON users(employee_code);