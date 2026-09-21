-- V2__create_mission_table.sql

CREATE TABLE missions (
    id                          BIGINT IDENTITY(1,1) PRIMARY KEY,
    mission_code                VARCHAR(50) NULL UNIQUE,

    requester_id                BIGINT NOT NULL,
    requester_name              NVARCHAR(150) NOT NULL,

    position                    NVARCHAR(100) NOT NULL,
    function_name               NVARCHAR(100) NOT NULL,
    business                    NVARCHAR(100) NOT NULL,
    job_level                   VARCHAR(50) NOT NULL,

    based_location              NVARCHAR(150) NOT NULL,
    destination_location        NVARCHAR(150) NOT NULL,
    location_tier               VARCHAR(20) NOT NULL,

    travel_objectives           NVARCHAR(MAX) NOT NULL,

    departure_date              DATE NOT NULL,
    departure_time              TIME NULL,
    arrival_date                DATE NOT NULL,
    arrival_time                TIME NULL,
    number_of_travel_days       INT NOT NULL,

    breakfast_amount            DECIMAL(18,2) NULL,
    breakfast_quantity          INT NULL,
    breakfast_total             DECIMAL(18,2) NULL,

    lunch_amount                DECIMAL(18,2) NULL,
    lunch_quantity              INT NULL,
    lunch_total                 DECIMAL(18,2) NULL,

    dinner_amount               DECIMAL(18,2) NULL,
    dinner_quantity             INT NULL,
    dinner_total                DECIMAL(18,2) NULL,

    accommodation_amount_per_night DECIMAL(18,2) NULL,
    number_of_night_stay        INT NULL,
    accommodation_total         DECIMAL(18,2) NULL,

    total_expense               DECIMAL(18,2) NULL,

    description                 NVARCHAR(MAX) NULL,

    status                      VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    current_approval_step       VARCHAR(30) NULL,

    created_at                  DATETIMEOFFSET(6) NOT NULL DEFAULT SYSUTCDATETIME(),
    updated_at                  DATETIMEOFFSET(6) NOT NULL DEFAULT SYSUTCDATETIME(),
    created_by                  BIGINT NULL,
    updated_by                  BIGINT NULL,

    CONSTRAINT fk_missions_requester
        FOREIGN KEY (requester_id) REFERENCES users(id),

    CONSTRAINT fk_missions_created_by
        FOREIGN KEY (created_by) REFERENCES users(id),

    CONSTRAINT fk_missions_updated_by
        FOREIGN KEY (updated_by) REFERENCES users(id)
);

CREATE INDEX idx_missions_requester_id
    ON missions(requester_id);

CREATE INDEX idx_missions_status
    ON missions(status);

CREATE INDEX idx_missions_departure_date
    ON missions(departure_date);
