-- Create a new user with restricted privileges
CREATE USER 'it_user'@'%' IDENTIFIED BY 'it_password';
GRANT ALL PRIVILEGES ON test.* TO 'it_user'@'%';
GRANT ALL PRIVILEGES ON Schema1.* TO 'it_user'@'%';
FLUSH PRIVILEGES;

-- TODO beschreiben
SET time_zone = 'UTC';

-- Create Schema1
CREATE SCHEMA IF NOT EXISTS Schema1;
USE Schema1;

CREATE TABLE IF NOT EXISTS ExampleDataTable
(
    -- String-Datentypen
    char_column       CHAR(10),
    varchar_column    VARCHAR(255),
    tinytext_column   TINYTEXT,
    text_column       TEXT,
    mediumtext_column MEDIUMTEXT,
    longtext_column   LONGTEXT,
    enum_column       ENUM ('value1', 'value2', 'value3'),
    set_column        SET ('option1', 'option2', 'option3'),
    binary_column     BINARY(6),
    varbinary_column  VARBINARY(255),

    -- Numerische Datentypen
    bit_column        BIT(1),
    tinyint_column    TINYINT,
    smallint_column   SMALLINT,
    mediumint_column  MEDIUMINT,
    int_column        INT,
    bigint_column     BIGINT,
    decimal_column    DECIMAL(10, 2),
    float_column      FLOAT,
    double_column     DOUBLE,
    boolean_column    BOOLEAN,

    -- Date/Time Datentypen
    date_column       DATE,
    datetime_column   DATETIME,
    timestamp_column  TIMESTAMP,
    time_column       TIME,
    year_column       YEAR,

    -- Large Object Datentypen
    tinyblob_column   TINYBLOB,
    blob_column       BLOB,
    mediumblob_column MEDIUMBLOB,
    longblob_column   LONGBLOB
);

INSERT INTO ExampleDataTable
VALUES ('abc',
        'varchar example',
        'tinytext example',
        'text example',
        'mediumtext example',
        'longtext example',
        'value1',
        'option1,option2',
        BINARY 'binary',
        BINARY 'varbinary',
        b'1',
        42,
        32767,
        8388607,
        2147483647,
        9223372036854775807,
        123.45,
        123.45,
        123.45,
        TRUE,
        '2022-01-01',
        '2022-01-01 12:34:56',
        '2022-01-01 12:34:56',
        '12:34:56',
        2022,
        BINARY 'tinyblob data',
        'blob data',
        'mediumblob data',
        'longblob data');