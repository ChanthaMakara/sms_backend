CREATE TABLE approval_history (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    mission_id BIGINT NOT NULL,
    step VARCHAR(30) NOT NULL,
    decision VARCHAR(20) NOT NULL,
    comment VARCHAR(1000) NULL,
    decided_by BIGINT NOT NULL,
    decided_at DATETIMEOFFSET(6) NOT NULL DEFAULT SYSDATETIMEOFFSET(),
    CONSTRAINT FK_approval_history_mission FOREIGN KEY (mission_id) REFERENCES missions(id)
);