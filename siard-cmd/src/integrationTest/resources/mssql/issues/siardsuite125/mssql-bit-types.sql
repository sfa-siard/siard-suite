CREATE SCHEMA [BitSchema];

-- Table creation with different bit columns
CREATE TABLE [BitSchema].[BitTest] (
    [id] INT PRIMARY KEY,
    [bit1] BIT,             -- Single bit (0 or 1)
    [bit_array_small] BINARY(8),  -- Fixed-length binary data of exactly 8 bytes (SQL Server BIT type only stores single bits)
    [bit_array_large] BINARY(64)  -- Fixed-length binary data of exactly 64 bytes
    );

-- Test data with different bit patterns
INSERT INTO [BitSchema].[BitTest] ([id], [bit1], [bit_array_small], [bit_array_large])
VALUES
    (1, 1, 0x00AA, 0x00AABBCCDD),
    (2, 0, 0x00FF, 0x0001020304);

-- Simple function that returns a bit value
CREATE FUNCTION [BitSchema].[GetBitValue](
    @value INT
)
RETURNS BIT
AS
BEGIN
    DECLARE @result BIT;

    IF @value > 0
        SET @result = 1;
    ELSE
        SET @result = 0;

    RETURN @result;
END;

-- Simple stored procedure that works with bit values
CREATE PROCEDURE [BitSchema].[ProcessBit]
    @input_bit BIT,
    @output_bit BIT OUTPUT,
    @bit_count INT OUTPUT
AS
BEGIN
    -- Toggle the bit
    IF @input_bit = 1
        SET @output_bit = 0;
    ELSE
        SET @output_bit = 1;

    -- Set the count to 1 if the output is 1, otherwise 0
    IF @output_bit = 1
        SET @bit_count = 1;
    ELSE
        SET @bit_count = 0;
END;
