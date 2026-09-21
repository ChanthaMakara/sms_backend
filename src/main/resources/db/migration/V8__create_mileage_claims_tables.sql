CREATE TABLE mileage_claims (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,

    mission_id BIGINT NOT NULL,
    vehicle_request_id BIGINT,

    requester_name NVARCHAR(255) NOT NULL,
    requester_id NVARCHAR(100),
    position NVARCHAR(255),
    function_name NVARCHAR(255),
    business NVARCHAR(255),
    job_level NVARCHAR(50),

    based_location NVARCHAR(255),
    destination_location NVARCHAR(255),
    travel_start_date DATE NOT NULL,
    travel_end_date DATE NOT NULL,

    travel_objectives NVARCHAR(MAX) NOT NULL,

    total_distance_km DECIMAL(10,2) NOT NULL,
    total_claim_amount DECIMAL(18,2) NOT NULL,

    status NVARCHAR(30) NOT NULL DEFAULT 'DRAFT',

    created_at DATETIMEOFFSET(6) NOT NULL DEFAULT SYSDATETIMEOFFSET(),
    updated_at DATETIMEOFFSET(6) NOT NULL DEFAULT SYSDATETIMEOFFSET(),

    CONSTRAINT fk_mileage_claims_mission
        FOREIGN KEY (mission_id)
        REFERENCES missions(id),

    CONSTRAINT fk_mileage_claims_vehicle_request
        FOREIGN KEY (vehicle_request_id)
        REFERENCES vehicle_requests(id)
);

CREATE TABLE mileage_travel_details (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,

    mileage_claim_id BIGINT NOT NULL,

    travel_date DATE NOT NULL,
    origin NVARCHAR(255) NOT NULL,
    destination NVARCHAR(255) NOT NULL,
    purpose_of_travel NVARCHAR(MAX),
    distance_km DECIMAL(10,2) NOT NULL,
    remarks NVARCHAR(MAX),

    CONSTRAINT fk_mileage_travel_details_mileage_claim
        FOREIGN KEY (mileage_claim_id)
        REFERENCES mileage_claims(id)
);