CREATE SCHEMA [DataTypeMappingTest];

CREATE TABLE [DataTypeMappingTest].[datatype_mapping_test_table]
(
    [id]               INT PRIMARY KEY,

    -- Character types
    [char_col]         CHAR,
    [char_n_col]       CHAR(10),
    [varchar_col]      VARCHAR,
    [varchar_n_col]    VARCHAR(100),
    [text_col]         TEXT,
    [nchar_col]        NCHAR,
    [nchar_n_col]      NCHAR(10),
    [nvarchar_col]     NVARCHAR,
    [nvarchar_n_col]   NVARCHAR(100),
    [ntext_col]        NTEXT,
    [xml_col]          XML,

    -- Integer types
    [tinyint_col]      TINYINT,
    [smallint_col]     SMALLINT,
    [int_col]          INT,
    [bigint_col]       BIGINT,

    -- Numeric types
    [numeric_col]      NUMERIC,
    [numeric_p_col]    NUMERIC(10),
    [numeric_ps_col]   NUMERIC(10, 2),
    [decimal_col]      DECIMAL,
    [decimal_p_col]    DECIMAL(10),
    [decimal_ps_col]   DECIMAL(10, 2),
    [smallmoney_col]   SMALLMONEY,
    [money_col]        MONEY,
    [float_col]        FLOAT,
    [float_p_small_col] FLOAT(7),
    [float_p_large_col] FLOAT(10),
    [real_col]         REAL,

    -- Bit type
    [bit_col]          BIT,

    -- Binary types
    [binary_col]       BINARY,
    [binary_n_col]     BINARY(16),
    [varbinary_col]    VARBINARY,
    [varbinary_n_col]  VARBINARY(100),
    [image_col]        IMAGE,

    -- Date/Time types
    [date_col]         DATE,
    [time_col]         TIME,
    [datetime_col]     DATETIME,
    [datetime2_col]    DATETIME2,
    [smalldatetime_col] SMALLDATETIME
);

INSERT INTO [DataTypeMappingTest].[datatype_mapping_test_table]
    ([id],
     [char_col], [char_n_col], [varchar_col], [varchar_n_col], [text_col],
     [nchar_col], [nchar_n_col], [nvarchar_col], [nvarchar_n_col], [ntext_col], [xml_col],
     [tinyint_col], [smallint_col], [int_col], [bigint_col],
     [numeric_col], [numeric_p_col], [numeric_ps_col],
     [decimal_col], [decimal_p_col], [decimal_ps_col],
     [smallmoney_col], [money_col],
     [float_col], [float_p_small_col], [float_p_large_col], [real_col],
     [bit_col],
     [binary_col], [binary_n_col], [varbinary_col], [varbinary_n_col], [image_col],
     [date_col], [time_col], [datetime_col], [datetime2_col], [smalldatetime_col])
VALUES
    (1,
     'A',                                                -- CHAR(1)
     'ABCDEFGHIJ',                                       -- CHAR(10)
     'V',                                                -- VARCHAR(1)
     'Hello MSSQL',                                      -- VARCHAR(100)
     'This is a text column',                            -- TEXT
     N'N',                                               -- NCHAR(1)
     N'ABCDEFGHIJ',                                      -- NCHAR(10)
     N'N',                                               -- NVARCHAR(1)
     N'Hello Unicode',                                   -- NVARCHAR(100)
     N'This is ntext',                                   -- NTEXT
     '<root><element>value</element></root>',            -- XML
     255,                                                -- TINYINT
     32767,                                              -- SMALLINT
     2147483647,                                         -- INT
     9223372036854775807,                                -- BIGINT
     12345678901234567,                                  -- NUMERIC
     1234567890,                                         -- NUMERIC(10)
     12345678.90,                                        -- NUMERIC(10,2)
     12345678901234567,                                  -- DECIMAL
     1234567890,                                         -- DECIMAL(10)
     12345678.90,                                        -- DECIMAL(10,2)
     214748.3647,                                        -- SMALLMONEY
     922337203685477.5807,                               -- MONEY
     1.23456789012345E+100,                              -- FLOAT
     1.2345,                                             -- FLOAT(7) p<=7 => real
     1.2345E+10,                                         -- FLOAT(10) p>7 => float
     1.2345,                                             -- REAL
     1,                                                  -- BIT
     0x01,                                               -- BINARY(1)
     0x00112233445566778899AABBCCDDEEFF,                 -- BINARY(16)
     0x01,                                               -- VARBINARY(1)
     0xA1B2C3,                                           -- VARBINARY(100)
     0xDEADBEEF,                                         -- IMAGE
     '2025-09-04',                                       -- DATE
     '15:30:45',                                         -- TIME
     '2025-09-04 15:30:45',                              -- DATETIME
     '2025-09-04 15:30:45.1234567',                      -- DATETIME2
     '2025-09-04 15:30:00'                               -- SMALLDATETIME
    );
