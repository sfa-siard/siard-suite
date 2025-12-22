ALTER SESSION SET CONTAINER=XEPDB1;

-- Create users
CREATE USER Schema1 IDENTIFIED BY password QUOTA UNLIMITED ON USERS;
GRANT CONNECT, RESOURCE TO Schema1;
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE, CREATE TRIGGER, CREATE PROCEDURE TO Schema1;

CREATE USER Schema2 IDENTIFIED BY password QUOTA UNLIMITED ON USERS;
GRANT CONNECT, RESOURCE TO Schema2;
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE, CREATE TRIGGER, CREATE PROCEDURE TO Schema2;

-- Create Teams table in Schema1
CREATE TABLE Schema1.Teams
(
    TeamName VARCHAR2(50),
    Location VARCHAR2(50),
    CONSTRAINT PK_Teams PRIMARY KEY (TeamName, Location)
);

-- Insert data into Teams table
INSERT INTO Schema1.Teams (Location, TeamName)
VALUES ('Bern', 'd3');

INSERT INTO Schema1.Teams (Location, TeamName)
VALUES ('Bern', 'mobility');

INSERT INTO Schema1.Teams (Location, TeamName)
VALUES ('Zürich', 'd3');

-- Create Members table in Schema2
CREATE TABLE Schema2.Members
(
    MemberID   NUMBER,
    MemberName VARCHAR2(50),
    CONSTRAINT PK_Members PRIMARY KEY (MemberID)
);

-- Insert data into Members table
INSERT INTO Schema2.Members (MemberID, MemberName)
VALUES (1, 'John Doe');

INSERT INTO Schema2.Members (MemberID, MemberName)
VALUES (2, 'Jane Smith');

INSERT INTO Schema2.Members (MemberID, MemberName)
VALUES (3, 'Bob Johnson');

-- Create TeamMembers table in Schema2
CREATE TABLE Schema2.TeamMembers
(
    TeamMembersID NUMBER,
    TeamName      VARCHAR2(50),
    Location      VARCHAR2(50),
    MemberID      NUMBER,
    CONSTRAINT PK_TeamMembers PRIMARY KEY (TeamMembersID),
    CONSTRAINT FK_TeamMembers_TeamID
        FOREIGN KEY (TeamName, Location)
            REFERENCES Schema1.Teams (TeamName, Location)
            ON DELETE CASCADE,
    CONSTRAINT FK_TeamMembers_MemberID
        FOREIGN KEY (MemberID)
            REFERENCES Schema2.Members (MemberID)
            ON DELETE SET NULL
);

-- Insert data into TeamMembers table
INSERT INTO Schema2.TeamMembers (TeamMembersID, Location, TeamName, MemberID)
VALUES (1, 'Bern', 'd3', 1);

INSERT INTO Schema2.TeamMembers (TeamMembersID, Location, TeamName, MemberID)
VALUES (2, 'Bern', 'mobility', 2);

INSERT INTO Schema2.TeamMembers (TeamMembersID, Location, TeamName, MemberID)
VALUES (3, 'Bern', 'd3', 3);

COMMIT;
