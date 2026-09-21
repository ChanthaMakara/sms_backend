CREATE TABLE settlements (
    id                      BIGINT IDENTITY(1,1) PRIMARY KEY,
    mission_id              BIGINT NOT NULL,
    total_allowance         DECIMAL(18,2) NOT NULL,
    total_mileage_claim     DECIMAL(18,2) NOT NULL,
    grand_total             DECIMAL(18,2) NOT NULL,
    status                  NVARCHAR(30) NOT NULL,
    settled_at              DATETIMEOFFSET(6) NULL,
    settled_by              BIGINT NULL,
    notes                   NVARCHAR(MAX) NULL,
    created_at              DATETIMEOFFSET(6) NOT NULL,
    updated_at              DATETIMEOFFSET(6) NOT NULL,

    CONSTRAINT fk_settlements_mission FOREIGN KEY (mission_id) REFERENCES missions(id),
    CONSTRAINT uq_settlements_mission UNIQUE (mission_id)
);