-- Schema creation
CREATE SCHEMA IF NOT EXISTS bitschema;

-- Set search path to the schema
SET search_path TO bitschema;

-- Table creation with different bit columns
CREATE TABLE bittest
(
    id    INT PRIMARY KEY,
    bit1  BIT(1), -- Single bit (0 or 1)
    bit8  BIT(8), -- 8 bits (0 to 255)
    bit64 BIT(64) -- 64 bits
);

-- Test data with different bit patterns
INSERT INTO bittest
VALUES (1, B'1', B'10101010', B'1111111111111111111111111111111111111111111111111111111111111111');
INSERT INTO bittest
VALUES (2, B'0', B'00000001', B'0000000000000000000000000000000000000000000000000000000000000001');

-- Function definition
CREATE OR REPLACE FUNCTION toggle_bits(
    bits BIT(8), -- 8-bit input
    toggle_mask BIT(8), -- 8-bit mask
    reverse BIT(1) -- Single bit control
) RETURNS BIT(8) AS
$$
DECLARE
    result BIT(8);
BEGIN
    -- Start with original bits
    result := bits;

    -- Toggle bits where mask is 1
    result := result # toggle_mask;

    -- If reverse is 1, reverse all bits
    IF reverse = 1 THEN
        result := ~result;
    END IF;

    RETURN result;
END;
$$ LANGUAGE plpgsql;

-- Procedure definition
CREATE OR REPLACE PROCEDURE process_bits_proc(
    IN input_bits BIT(8),
    IN toggle_mask BIT(8),
    IN reverse BIT(1),
    INOUT result_bits BIT(8),
    INOUT bit_count INTEGER
)
    LANGUAGE plpgsql
AS
$$
DECLARE
    temp_bits BIT(8);
BEGIN
    -- Toggle bits where mask is 1
    temp_bits := input_bits # toggle_mask;

    -- Reverse bits if reverse bit is '1'
    IF reverse::TEXT = '1' THEN
        temp_bits := ~temp_bits;
    END IF;

    -- Set output parameters (INOUT)
    result_bits := temp_bits;
    bit_count := LENGTH(REPLACE(temp_bits::TEXT, '0', ''));
END;
$$;
