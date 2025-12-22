-- Create a new user with restricted privileges
CREATE USER 'it_user'@'%' IDENTIFIED BY 'it_password';
GRANT ALL PRIVILEGES ON test.* TO 'it_user'@'%';
GRANT ALL PRIVILEGES ON Schema1.* TO 'it_user'@'%';
GRANT ALL PRIVILEGES ON Schema2.* TO 'it_user'@'%';
FLUSH PRIVILEGES;

-- Create Schema1
CREATE SCHEMA IF NOT EXISTS Schema1;
USE Schema1;

-- Create Teams table in Schema1
CREATE TABLE IF NOT EXISTS Teams
(
    TeamName VARCHAR(50),
    Location VARCHAR(50),
    CONSTRAINT PK_Teams PRIMARY KEY (TeamName, Location)
);

-- Insert data into Teams table
INSERT INTO Teams (Location, TeamName)
VALUES ('Bern', 'd3'),
       ('Bern', 'mobility'),
       ('Zürich', 'd3');

-- Create Schema2
CREATE SCHEMA IF NOT EXISTS Schema2;
USE Schema2;

-- Create Members table in Schema2
CREATE TABLE IF NOT EXISTS Members
(
    MemberID   INT,
    MemberName VARCHAR(50),
    CONSTRAINT PK_Members PRIMARY KEY (MemberID)
);

-- Insert data into Members table
INSERT INTO Members (MemberID, MemberName)
VALUES (1, 'John Doe'),
       (2, 'Jane Smith'),
       (3, 'Bob Johnson');

-- Create TeamMembers table in Schema2
CREATE TABLE IF NOT EXISTS TeamMembers
(
    TeamMembersID INT,
    TeamName      VARCHAR(50),
    Location      VARCHAR(50),
    MemberID      INT,
    CONSTRAINT PK_TeamMembers PRIMARY KEY (TeamMembersID),
    CONSTRAINT FK_TeamMembers_TeamID
        FOREIGN KEY (TeamName, Location)
            REFERENCES Schema1.Teams (TeamName, Location)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    CONSTRAINT FK_TeamMembers_MemberID
        FOREIGN KEY (MemberID)
            REFERENCES Schema2.Members (MemberID)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
);

-- Insert data into TeamMembers table
INSERT INTO TeamMembers (TeamMembersID, Location, TeamName, MemberID)
VALUES (1, 'Bern', 'd3', 1),
       (2, 'Bern', 'mobility', 2),
       (3, 'Bern', 'd3', 3);

