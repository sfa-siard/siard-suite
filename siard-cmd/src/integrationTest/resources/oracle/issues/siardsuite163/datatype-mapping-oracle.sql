ALTER SESSION SET CONTAINER=XEPDB1;

CREATE TABLE IT_USER.datatype_mapping_test
(
    id                     NUMBER PRIMARY KEY,
    int_col                INTEGER, -- Numeric types
    num_col                NUMBER,
    num_prec_col           NUMBER(10),
    num_prec_scale_col     NUMBER(10, 2),
    smallint_col           SMALLINT,
    decimal_col            DECIMAL,
    decimal_prec_scale_col DECIMAL(10, 2),
    numeric_col            NUMERIC,
    numeric_prec_scale_col NUMERIC(10, 2),
    float_col              FLOAT(10),
    real_col               REAL,
    double_prec_col        DOUBLE PRECISION,
    binary_float_col       BINARY_FLOAT,
    binary_double_col      BINARY_DOUBLE,
    char_col               CHAR,    -- Character types
    char_n_col             CHAR(10),
    varchar_col            VARCHAR(100),
    varchar2_col           VARCHAR2(100),
    nchar_col              NCHAR,
    nchar_n_col            NCHAR(10),
    nvarchar2_col          NVARCHAR2(100),
    clob_col               CLOB,    -- LOB types
    nclob_col              NCLOB,
    blob_col               BLOB,
    raw_col                RAW(100),
    longraw_col            LONG RAW,
    date_col               DATE,    -- Date/Time types
    timestamp_col          TIMESTAMP,
    timestamp_n_col        TIMESTAMP(6),
    timestamp_tz_col       TIMESTAMP WITH TIME ZONE,
    timestamp_ltz_col      TIMESTAMP WITH LOCAL TIME ZONE
);

INSERT INTO IT_USER.datatype_mapping_test
VALUES (1,
        12345, -- INTEGER
        9876543210.12345, -- NUMBER (no prec/scale)
        9876543210, -- NUMBER(10)
        1234.56, -- NUMBER(10,2)
        123, -- SMALLINT
        5678, -- DECIMAL (no prec/scale)
        1234.56, -- DECIMAL(10,2)
        9012, -- NUMERIC (no prec/scale)
        1234.56, -- NUMERIC(10,2)
        1.2345E+10, -- FLOAT(10)
        1.2345, -- REAL
        1.23456789012345E+100, -- DOUBLE PRECISION
        1.2345E+10, -- BINARY_FLOAT
        1.23456789012345E+100, -- BINARY_DOUBLE
        'A', -- CHAR(1)
        'TENCHARS', -- CHAR(10)
        'VARCHAR test', -- VARCHAR(100)
        'Variable length string up to 100 chars', -- VARCHAR2(100)
        N'Ü', -- NCHAR(1)
        N'ÜNICHAR10', -- NCHAR(10)
        N'Unicode variable length string', -- NVARCHAR2(100)
        'Character large object data', -- CLOB
        N'Unicode CLOB data', -- NCLOB
        HEXTORAW('1A2B3C4D5E6F'), -- BLOB
        HEXTORAW('1A2B3C4D'), -- RAW(100)
        HEXTORAW('1A2B3C4D5E6F'), -- LONG RAW
        TO_DATE('2025-08-28', 'YYYY-MM-DD'), -- DATE
        TO_TIMESTAMP('2025-08-28 15:30:45', 'YYYY-MM-DD HH24:MI:SS'), -- TIMESTAMP
        SYSTIMESTAMP, -- TIMESTAMP(6)
        TO_TIMESTAMP_TZ('2025-08-28 15:30:45 +02:00', 'YYYY-MM-DD HH24:MI:SS TZH:TZM'), -- TIMESTAMP WITH TIME ZONE
        SYSTIMESTAMP -- TIMESTAMP WITH LOCAL TIME ZONE
       );