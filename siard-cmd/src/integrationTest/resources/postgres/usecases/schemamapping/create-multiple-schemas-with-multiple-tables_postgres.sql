-- Create Schema1
CREATE SCHEMA Schema1;

-- Create tables in Schema1
CREATE TABLE Schema1.Table1
(
    ID   SERIAL PRIMARY KEY,
    Name VARCHAR(50)
);

CREATE TABLE Schema1.Table2
(
    ID          SERIAL PRIMARY KEY,
    Description TEXT
);

-- Insert data into Schema1.Table1
INSERT INTO Schema1.Table1 (Name)
VALUES ('John Doe'),
       ('Jane Smith'),
       ('Bob Johnson');

-- Insert data into Schema1.Table2
INSERT INTO Schema1.Table2 (Description)
VALUES ('Sample description 1'),
       ('Sample description 2'),
       ('Sample description 3');


-- Create Schema2
CREATE SCHEMA Schema2;

-- Create tables in Schema2
CREATE TABLE Schema2.Table3
(
    ID       SERIAL PRIMARY KEY,
    Category VARCHAR(50)
);

CREATE TABLE Schema2.Table4
(
    ID       SERIAL PRIMARY KEY,
    Quantity INT
);

-- Insert data into Schema2.Table3
INSERT INTO Schema2.Table3 (Category)
VALUES ('Category A'),
       ('Category B'),
       ('Category C');

-- Insert data into Schema2.Table4
INSERT INTO Schema2.Table4 (Quantity)
VALUES (10),
       (20),
       (30);

-- Additional comments
COMMENT ON TABLE Schema1.Table1 IS 'This is Table1 in Schema1.';
COMMENT ON COLUMN Schema1.Table1.ID IS 'Primary key for Table1.';

COMMENT ON TABLE Schema2.Table3 IS 'This is Table3 in Schema2.';
COMMENT ON COLUMN Schema2.Table3.ID IS 'Primary key for Table3.';
