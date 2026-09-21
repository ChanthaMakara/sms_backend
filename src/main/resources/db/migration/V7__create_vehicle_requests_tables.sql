CREATE TABLE vehicle_requests (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,

    mission_id BIGINT NOT NULL,

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

    status NVARCHAR(30) NOT NULL DEFAULT 'DRAFT',

    created_at DATETIMEOFFSET(6) NOT NULL DEFAULT SYSDATETIMEOFFSET(),
    updated_at DATETIMEOFFSET(6) NOT NULL DEFAULT SYSDATETIMEOFFSET(),

    CONSTRAINT fk_vehicle_requests_mission
        FOREIGN KEY (mission_id)
        REFERENCES missions(id)
);

CREATE TABLE vehicle_travel_details (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,

    vehicle_request_id BIGINT NOT NULL,

    travel_date DATE NOT NULL,
    origin NVARCHAR(255) NOT NULL,
    destination NVARCHAR(255) NOT NULL,
    purpose_of_travel NVARCHAR(MAX),
    distance_km DECIMAL(10,2) NOT NULL,
    remarks NVARCHAR(MAX),

    CONSTRAINT fk_vehicle_travel_details_vehicle_request
        FOREIGN KEY (vehicle_request_id)
        REFERENCES vehicle_requests(id)
);