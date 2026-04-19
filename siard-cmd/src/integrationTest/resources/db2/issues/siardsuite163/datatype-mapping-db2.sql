CREATE TABLE DATATYPE_MAPPING_TEST
(
    ID                        INTEGER NOT NULL PRIMARY KEY,

    -- Character types
    CHAR_COL                  CHAR,
    CHAR_N_COL                CHAR(10),
    VARCHAR_N_COL             VARCHAR(100),
    CLOB_COL                  CLOB,
    XML_COL                   XML,

    -- Graphic types
    GRAPHIC_COL               GRAPHIC,
    GRAPHIC_N_COL             GRAPHIC(10),
    VARGRAPHIC_N_COL          VARGRAPHIC(100),
    DBCLOB_COL                DBCLOB,

    -- Binary string types
    CHAR_BIT_COL              CHAR(1) FOR BIT DATA,
    CHAR_N_BIT_COL            CHAR(16) FOR BIT DATA,
    VARCHAR_N_BIT_COL         VARCHAR(100) FOR BIT DATA,
    BLOB_COL                  BLOB,

    -- Integer types
    SMALLINT_COL              SMALLINT,
    INTEGER_COL               INTEGER,
    BIGINT_COL                BIGINT,

    -- Numeric types
    NUMERIC_COL               NUMERIC,
    NUMERIC_P_COL             NUMERIC(10),
    NUMERIC_PS_COL            NUMERIC(10, 2),
    DECIMAL_COL               DECIMAL,
    DECIMAL_P_COL             DECIMAL(10),
    DECIMAL_PS_COL            DECIMAL(10, 2),

    -- Floating-point types
    FLOAT_COL                 FLOAT,
    FLOAT_P_SMALL_COL         FLOAT(7),
    FLOAT_P_LARGE_COL         FLOAT(10),
    REAL_COL                  REAL,
    DOUBLE_COL                DOUBLE,

    -- Date/Time types
    DATE_COL                  DATE,
    TIME_COL                  TIME,
    TIMESTAMP_COL             TIMESTAMP,
    TIMESTAMP_N_COL           TIMESTAMP(9)
);

INSERT INTO DATATYPE_MAPPING_TEST
    (ID,
     CHAR_COL, CHAR_N_COL, VARCHAR_N_COL, CLOB_COL,
     GRAPHIC_COL, GRAPHIC_N_COL, VARGRAPHIC_N_COL, DBCLOB_COL,
     CHAR_BIT_COL, CHAR_N_BIT_COL, VARCHAR_N_BIT_COL, BLOB_COL,
     SMALLINT_COL, INTEGER_COL, BIGINT_COL,
     NUMERIC_COL, NUMERIC_P_COL, NUMERIC_PS_COL,
     DECIMAL_COL, DECIMAL_P_COL, DECIMAL_PS_COL,
     FLOAT_COL, FLOAT_P_SMALL_COL, FLOAT_P_LARGE_COL, REAL_COL, DOUBLE_COL,
     DATE_COL, TIME_COL, TIMESTAMP_COL, TIMESTAMP_N_COL)
VALUES
    (1,
     'A',                                                -- CHAR(1)
     'ABCDEFGHIJ',                                       -- CHAR(10)
     'Hello DB2',                                        -- VARCHAR(100)
     'This is a clob value',                             -- CLOB
     G'A',                                               -- GRAPHIC(1)
     G'ABCDEFGHIJ',                                      -- GRAPHIC(10)
     G'Hello',                                           -- VARGRAPHIC(100)
     G'This is a dbclob',                                -- DBCLOB
     X'01',                                              -- CHAR(1) FOR BIT DATA
     X'00112233445566778899AABBCCDDEEFF',                -- CHAR(16) FOR BIT DATA
     X'A1B2C3',                                          -- VARCHAR(100) FOR BIT DATA
     BLOB(X'CAFEBABE'),                                  -- BLOB
     32767,                                              -- SMALLINT
     2147483647,                                         -- INTEGER
     9223372036854775807,                                -- BIGINT
     12345,                                              -- NUMERIC
     1234567890,                                         -- NUMERIC(10)
     12345678.90,                                        -- NUMERIC(10,2)
     12345,                                              -- DECIMAL
     1234567890,                                         -- DECIMAL(10)
     12345678.90,                                        -- DECIMAL(10,2)
     1.23456789012345E+100,                              -- FLOAT
     1.2345,                                             -- FLOAT(7)
     1.2345E+10,                                         -- FLOAT(10)
     1.2345,                                             -- REAL
     1.23456789012345E+100,                              -- DOUBLE
     '2025-09-04',                                       -- DATE
     '15.30.45',                                         -- TIME
     '2025-09-04-15.30.45.123456',                       -- TIMESTAMP
     '2025-09-04-15.30.45.123456789'                     -- TIMESTAMP(9)
    );
