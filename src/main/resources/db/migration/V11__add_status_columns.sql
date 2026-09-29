ALTER TABLE activity_reports
    ADD status NVARCHAR(30) NOT NULL CONSTRAINT DF_activity_reports_status DEFAULT 'DRAFT';

UPDATE settlements SET status = 'PAID' WHERE status = 'SETTLED';
