ALTER SESSION SET CONTAINER=XEPDB1;

-- Oracle does not have BIT types
CREATE TABLE IT_USER.oracle_binary_test (
    id NUMBER PRIMARY KEY,
    bit_equivalent NUMBER(1),    -- Equivalent to BIT(1) - stores 0 or 1
    raw_small RAW(1),           -- Single byte raw data - equivalent to BIT(8)
    raw_medium RAW(8),          -- 8 bytes raw data - equivalent to BIT(64)
    raw_large RAW(2000),        -- Large raw data
    blob_data BLOB              -- Binary Large Object for large binary data
);

-- Test data with different binary patterns
INSERT INTO IT_USER.oracle_binary_test VALUES (
    1, 
    1,
    HEXTORAW('FF'),
    HEXTORAW('AAAAAAAAAAAAAAAA'),
    HEXTORAW('DEADBEEF'),
    HEXTORAW('CAFEBABE')
);

INSERT INTO IT_USER.oracle_binary_test VALUES (
    2,
    0,
    HEXTORAW('00'),
    HEXTORAW('0000000000000001'),
    HEXTORAW('FEDCBA9876543210'),
    HEXTORAW('0123456789ABCDEF')
);

-- Function definition
CREATE OR REPLACE FUNCTION IT_USER.toggle_raw_bits(
    raw_bits RAW,
    toggle_mask RAW,
    reverse_flag NUMBER
)
RETURN RAW
DETERMINISTIC
IS
    result RAW(8);
BEGIN
    -- Simple XOR operation on first byte
    IF raw_bits IS NOT NULL AND toggle_mask IS NOT NULL THEN
        result := HEXTORAW(LPAD(TO_CHAR(BITXOR(
            TO_NUMBER(SUBSTR(raw_bits, 1, 2), 'XX'),
            TO_NUMBER(SUBSTR(toggle_mask, 1, 2), 'XX')
        ), 'XX'), 2, '0'));

        -- Reverse if flag is set
        IF reverse_flag = 1 THEN
            result := HEXTORAW(LPAD(TO_CHAR(BITXOR(
                TO_NUMBER(SUBSTR(result, 1, 2), 'XX'), 255
            ), 'XX'), 2, '0'));
        END IF;
    ELSE
        result := raw_bits;
    END IF;

    RETURN result;
END toggle_raw_bits;
/

-- Procedure definition
CREATE OR REPLACE PROCEDURE IT_USER.process_raw_bits(
    input_raw IN RAW,
    toggle_mask IN RAW,
    reverse_flag IN NUMBER,
    result_raw OUT RAW,
    bit_count OUT NUMBER
)
IS
    byte_val NUMBER;
BEGIN
    -- Process the raw data
    result_raw := IT_USER.toggle_raw_bits(input_raw, toggle_mask, reverse_flag);

    -- Count bits (simple bit counting for first byte)
    IF result_raw IS NOT NULL THEN
        byte_val := TO_NUMBER(SUBSTR(result_raw, 1, 2), 'XX');
        bit_count := 0;

        -- Count 1-bits
        WHILE byte_val > 0 LOOP
            IF MOD(byte_val, 2) = 1 THEN
                bit_count := bit_count + 1;
            END IF;
            byte_val := FLOOR(byte_val / 2);
        END LOOP;
    ELSE
        bit_count := 0;
    END IF;
END process_raw_bits;
/
