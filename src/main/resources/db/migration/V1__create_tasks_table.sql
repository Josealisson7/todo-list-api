CREATE TABLE Tasks (
    TaskId INT IDENTITY(1,1) PRIMARY KEY,
    Title NVARCHAR(100) NOT NULL,
    Description NVARCHAR(MAX) NULL,
    CreatedAt DATETIME NOT NULL,
    Status VARCHAR(20) NOT NULL DEFAULT 'pending'
        CHECK (Status IN ('pending','progress','completed'))
);

CREATE INDEX IX_Tasks_Status ON Tasks(Status);
