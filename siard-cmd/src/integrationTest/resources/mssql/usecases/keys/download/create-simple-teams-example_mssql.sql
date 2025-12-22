CREATE SCHEMA "Schema1";

CREATE TABLE "Schema1"."Teams"
(
    "TeamName" VARCHAR(50),
    "Location" VARCHAR(50),
    CONSTRAINT "PK_Teams" PRIMARY KEY ("TeamName", "Location")
);

INSERT INTO "Schema1"."Teams" ("Location", "TeamName")
VALUES ('Bern', 'd3'),
       ('Bern', 'mobility'),
       ('Zürich', 'd3');

CREATE SCHEMA "Schema2";

CREATE TABLE "Schema2"."Members"
(
    "MemberID"   INT,
    "MemberName" VARCHAR(50),
    CONSTRAINT "PK_Members" PRIMARY KEY ("MemberID")
);

INSERT INTO "Schema2"."Members" ("MemberID", "MemberName")
VALUES (1, 'John Doe'),
       (2, 'Jane Smith'),
       (3, 'Bob Johnson');

CREATE TABLE "Schema2"."TeamMembers"
(
    "TeamMembersID" INT,
    "TeamName" VARCHAR(50),
    "Location" VARCHAR(50),
    "MemberID" INT,
    CONSTRAINT "PK_TeamMembers" PRIMARY KEY ("TeamMembersID"),
    CONSTRAINT "FK_TeamMembers_TeamID"
        FOREIGN KEY ("TeamName", "Location")
            REFERENCES "Schema1"."Teams" ("TeamName", "Location")
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    CONSTRAINT "FK_TeamMembers_MemberID"
        FOREIGN KEY ("MemberID")
            REFERENCES "Schema2"."Members" ("MemberID")
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
);

INSERT INTO "Schema2"."TeamMembers" ("TeamMembersID", "Location", "TeamName", "MemberID")
VALUES (1, 'Bern', 'd3', 1),
       (2, 'Bern', 'mobility', 2),
       (3, 'Bern', 'd3', 3);