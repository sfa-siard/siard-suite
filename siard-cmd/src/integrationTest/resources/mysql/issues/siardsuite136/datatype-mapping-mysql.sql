-- Create it_user with privileges for nation and test databases
CREATE USER 'it_user'@'%' IDENTIFIED BY 'it_password';
GRANT ALL PRIVILEGES ON datatype_mapping_test.* TO 'it_user'@'%';
GRANT ALL PRIVILEGES ON test.* TO 'it_user'@'%';

-- Schema creation - with underscore in the name
CREATE SCHEMA IF NOT EXISTS datatype_mapping_test;
USE datatype_mapping_test;

CREATE TABLE datatype_mapping_test_table
(
    id             INT PRIMARY KEY,

    -- Character types
    char_col       CHAR,
    char_n_col     CHAR(10),
    varchar_col    VARCHAR(100),
    tinytext_col   TINYTEXT,
    text_col       TEXT,
    mediumtext_col MEDIUMTEXT,

    -- Numeric types
    tinyint_col    TINYINT,
    smallint_col   SMALLINT,
    mediumint_col  MEDIUMINT,
    int_col        INT,
    bigint_col     BIGINT,
    decimal_col    DECIMAL,
    decimal_n_col  DECIMAL(10),
    decimal_pq_col DECIMAL(10, 2),
    numeric_col    NUMERIC,
    numeric_n_col  NUMERIC(10),
    numeric_pq_col NUMERIC(10, 2),
    float_col      FLOAT,
    float_p_col    FLOAT(10),
    float_pq_col   FLOAT(10, 2),
    double_col     DOUBLE,
    double_pq_col  DOUBLE(15, 5),

    -- Bit and binary types
    bit_col        BIT,
    bit_n_col      BIT(8),
    binary_col     BINARY(16),
    varbinary_col  VARBINARY(100),

    -- LOB / BLOB types
    tinyblob_col   TINYBLOB,
    blob_col       BLOB,
    mediumblob_col MEDIUMBLOB,
    longblob_col   LONGBLOB,

    -- Temporal types
    datetime_col   DATETIME,
    timestamp_col  TIMESTAMP,
    date_col       DATE,
    time_col       TIME,
    year_col       YEAR
);

-- Insert test data with comments
INSERT INTO datatype_mapping_test_table (id, char_col, char_n_col, varchar_col,
                                         tinytext_col, text_col, mediumtext_col,
                                         tinyint_col, smallint_col, mediumint_col, int_col, bigint_col,
                                         decimal_col, decimal_n_col, decimal_pq_col,
                                         numeric_col, numeric_n_col, numeric_pq_col,
                                         float_col, float_p_col, float_pq_col,
                                         double_col, double_pq_col,
                                         bit_col, bit_n_col,
                                         binary_col, varbinary_col,
                                         tinyblob_col, blob_col, mediumblob_col, longblob_col,
                                         datetime_col, timestamp_col, date_col, time_col, year_col)
VALUES (1, -- ID
        'A', -- CHAR(1)
        'ABCDEFGHIJ', -- CHAR(10)
        'Hello MySQL', -- VARCHAR(100)
        'Tiny text', -- TINYTEXT
        'Normal text column', -- TEXT
        'A much longer medium text value', -- MEDIUMTEXT

        127, -- TINYINT
        32767, -- SMALLINT
        8388607, -- MEDIUMINT
        2147483647, -- INT
        9223372036854775807, -- BIGINT

        12345.67, -- DECIMAL
        1234567890, -- DECIMAL(10)
        12345.67, -- DECIMAL(10,2)
        98765.43, -- NUMERIC
        54321, -- NUMERIC(10)
        54321.98, -- NUMERIC(10,2)

        1.23, -- FLOAT
        12345.6789, -- FLOAT(10)
        9876.54, -- FLOAT(10,2)
        1234567.89, -- DOUBLE
        1234567.89123, -- DOUBLE(15,5)

        b'1', -- BIT
        b'10101010', -- BIT(8)

        UNHEX('00112233445566778899AABBCCDDEEFF'), -- BINARY(16)
        UNHEX('A1B2C3'), -- VARBINARY(100)

        'tinyblob', -- TINYBLOB
        'this is a blob', -- BLOB
        REPEAT('A', 1000), -- MEDIUMBLOB
        REPEAT('B', 20000), -- LONGBLOB

        '2025-09-04 15:30:45', -- DATETIME
        CURRENT_TIMESTAMP, -- TIMESTAMP
        '2025-09-04', -- DATE
        '15:30:45', -- TIME
        2025 -- YEAR
       );