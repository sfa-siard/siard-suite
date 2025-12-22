-- Schema creation
CREATE SCHEMA IF NOT EXISTS precisiontypesschema;
SET
search_path TO precisiontypesschema;

-- Table creation with different precision types
CREATE TABLE typed_precision_test
(
    id    INT PRIMARY KEY,
    -- VARCHAR/CHAR/TEXT variations
    col_varchar_1    VARCHAR(1),
    col_varchar_255  VARCHAR(255),
    col_varchar_8000 VARCHAR(8000),
    -- PostgreSQL's TEXT type can store unlimited length text
    col_text         TEXT,
    col_char_10      CHAR(10),
    -- Numeric types with scale/precision
    col_numeric_10_2 NUMERIC(10, 2),
    col_numeric_8    NUMERIC(8)
);

-- Insert sample data
INSERT INTO typed_precision_test (id,
                                  col_varchar_1,
                                  col_varchar_255,
                                  col_varchar_8000,
                                  col_text,
                                  col_char_10,
                                  col_numeric_10_2,
                                  col_numeric_8)
VALUES (1,
        'X',
        'This is varchar(255)',
        'Longer varchar(8000)',
        'Unlimited text field',
        'abc',
        12345.67,
        99999999);