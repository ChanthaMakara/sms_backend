ALTER TABLE missions
    ADD mission_type VARCHAR(20) NOT NULL
        CONSTRAINT df_missions_mission_type DEFAULT 'INDIVIDUAL';

CREATE TABLE mission_participants (
    id                  BIGINT IDENTITY(1,1) PRIMARY KEY,
    mission_id          BIGINT        NOT NULL,
    employee_id         BIGINT        NOT NULL,
    employee_code       VARCHAR(50)   NULL,
    full_name           NVARCHAR(150) NOT NULL,
    job_level           VARCHAR(50)   NOT NULL,
    function_name       NVARCHAR(100) NULL,
    business            NVARCHAR(100) NULL,
    is_requester        BIT           NOT NULL DEFAULT 0,
    breakfast_total     DECIMAL(18,2) NOT NULL DEFAULT 0,
    lunch_total         DECIMAL(18,2) NOT NULL DEFAULT 0,
    dinner_total        DECIMAL(18,2) NOT NULL DEFAULT 0,
    accommodation_total DECIMAL(18,2) NOT NULL DEFAULT 0,
    total_expense       DECIMAL(18,2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_mp_mission  FOREIGN KEY (mission_id)  REFERENCES missions(id) ON DELETE CASCADE,
    CONSTRAINT fk_mp_employee FOREIGN KEY (employee_id) REFERENCES users(id),
    CONSTRAINT uq_mp_mission_employee UNIQUE (mission_id, employee_id)
);

CREATE INDEX ix_mp_mission ON mission_participants(mission_id);

INSERT INTO mission_participants
    (mission_id, employee_id, employee_code, full_name, job_level, function_name, business, is_requester,
     breakfast_total, lunch_total, dinner_total, accommodation_total, total_expense)
SELECT m.id, m.requester_id, u.employee_code, m.requester_name, m.job_level, m.function_name, m.business, 1,
       ISNULL(m.breakfast_total, 0), ISNULL(m.lunch_total, 0), ISNULL(m.dinner_total, 0),
       ISNULL(m.accommodation_total, 0), ISNULL(m.total_expense, 0)
FROM missions m
JOIN users u ON u.id = m.requester_id;
