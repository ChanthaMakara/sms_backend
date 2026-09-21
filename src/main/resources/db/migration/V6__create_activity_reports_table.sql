CREATE TABLE activity_reports (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,

    mission_id BIGINT NOT NULL,

    requester_name NVARCHAR(255) NOT NULL,
    requester_id NVARCHAR(100),
    position NVARCHAR(255),
    function_name NVARCHAR(255),
    business NVARCHAR(255),
    based_location NVARCHAR(255),
    destination_location NVARCHAR(255),

    travel_start_date DATE NOT NULL,
    travel_end_date DATE NOT NULL,

    travel_objectives NVARCHAR(MAX) NOT NULL,
    achieved_results NVARCHAR(MAX) NOT NULL,
    next_plan NVARCHAR(MAX),
    attached_documents NVARCHAR(MAX),

    requester_signature_date DATE,
    function_manager_comment NVARCHAR(MAX),
    function_manager_signature_date DATE,
    biz_ops_comment NVARCHAR(MAX),
    biz_ops_signature_date DATE,

    created_at DATETIMEOFFSET(6) NOT NULL DEFAULT SYSDATETIMEOFFSET(),
    updated_at DATETIMEOFFSET(6) NOT NULL DEFAULT SYSDATETIMEOFFSET(),

    CONSTRAINT fk_activity_reports_mission
        FOREIGN KEY (mission_id)
        REFERENCES missions(id)
);