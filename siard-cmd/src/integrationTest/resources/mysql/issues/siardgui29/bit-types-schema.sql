-- Schema creation
CREATE SCHEMA IF NOT EXISTS bitschema;
USE bitschema;

-- Table creation with different bit columns
CREATE TABLE bittest (
    id INT PRIMARY KEY,
    bit1 bit(1),        -- Single bit (0 or 1)
    bit8 bit(8),        -- 8 bits (0 to 255)
    bit64 bit(64)       -- 64 bits (maximum size)
);

-- Test data with different bit patterns
INSERT INTO bittest VALUES (1, b'1', b'10101010', b'1111111111111111111111111111111111111111111111111111111111111111');
INSERT INTO bittest VALUES (2, b'0', b'00000001', b'0000000000000000000000000000000000000000000000000000000000000001');

-- Function definition
CREATE FUNCTION toggle_bits(
    bits bit(8),        -- 8-bit input
    toggle_mask bit(8), -- 8-bit mask (1 means toggle, 0 means keep)
    reverse bit(1)      -- Single bit: 1 to reverse all bits, 0 to keep as is
)
    RETURNS bit(8)          -- Returns 8 bits
    DETERMINISTIC
BEGIN
    DECLARE result bit(8);

    -- Start with original bits
    SET result = bits;

    -- Toggle bits where mask is 1
    SET result = result ^ toggle_mask;

    -- If reverse is 1, reverse all bits
    IF reverse = 1 THEN
        SET result = ~result;
END IF;

RETURN result;
END;

-- Procedure definition
CREATE PROCEDURE process_bits(
    IN input_bits bit(8),        -- Input: 8-bit value
    IN toggle_mask bit(8),       -- Input: 8-bit mask
    IN reverse bit(1),           -- Input: Single bit control
    OUT result_bits bit(8),      -- Output: Result bits
    OUT bit_count tinyint        -- Output: Count of 1 bits
)
BEGIN
    -- Declare local variables
    DECLARE temp_bits bit(8);

    -- Start with input bits
    SET temp_bits = input_bits;

    -- Toggle bits where mask is 1
    SET temp_bits = temp_bits ^ toggle_mask;

    -- If reverse is 1, reverse all bits
    IF reverse = 1 THEN
        SET temp_bits = ~temp_bits;
END IF;

    -- Set the result bits
    SET result_bits = temp_bits;

    -- Count the number of 1 bits
    SET bit_count = BIT_COUNT(temp_bits);
END;