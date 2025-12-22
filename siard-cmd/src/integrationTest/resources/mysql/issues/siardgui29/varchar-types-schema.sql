-- Schema creation
CREATE SCHEMA IF NOT EXISTS varcharschema;
USE varcharschema;

-- Table creation with different varchar types
CREATE TABLE varchartest (
    id INT PRIMARY KEY,
    text2 VARCHAR(1),
    text3 VARCHAR(255),
    text4 VARCHAR(8000)
);

INSERT INTO varchartest (id, text2, text3, text4)
VALUES
    (1, 'b', 'Regular text', 'Another longer text example'),
    (2, 'd', 'Another text', 'Another longer text example');
