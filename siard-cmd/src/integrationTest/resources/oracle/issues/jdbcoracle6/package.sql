ALTER SESSION SET CONTAINER=XEPDB1;

CREATE USER testuser IDENTIFIED BY testpassword;

GRANT CREATE SESSION, CREATE TABLE, CREATE TYPE, CREATE PROCEDURE TO testuser;

ALTER SESSION SET CURRENT_SCHEMA = testuser;

-- at least one table is required - otherwise the archive step will fail
CREATE TABLE simple_table
(
    id    NUMBER,
    value VARCHAR2(100)
);

-- Used to cause java.io.IOException: Only one view with the same name allowed per schema!
-- Note that the error message is misleading due to a copy-paste error, and should be fixed in SiardApi#MetaSchemaImpl#createMetaRoutine

-- Package with overloading
CREATE OR REPLACE PACKAGE log_msg_overloaded AS
    -- Overloaded procedures
    PROCEDURE log_msg(p_msg VARCHAR2);
    PROCEDURE log_msg(p_msg VARCHAR2, p_level NUMBER);
    PROCEDURE log_msg(p_msg VARCHAR2, p_level NUMBER, p_user VARCHAR2);

    -- Overloaded functions
    FUNCTION calculate_area(p_radius NUMBER) RETURN NUMBER;
    FUNCTION calculate_area(p_length NUMBER, p_width NUMBER) RETURN NUMBER;
    FUNCTION calculate_area(p_length NUMBER, p_width NUMBER, p_height NUMBER) RETURN NUMBER;
END log_msg_overloaded;
/

-- Package without overloading
CREATE OR REPLACE PACKAGE simple_package AS
    PROCEDURE simple_proc(p_msg VARCHAR2);
END simple_package;
/

CREATE OR REPLACE PACKAGE BODY simple_package AS
    PROCEDURE simple_proc(p_msg VARCHAR2) AS
    BEGIN
        NULL;
    END;
END simple_package;
/

-- Standalone procedure with overloading
CREATE OR REPLACE PROCEDURE overloaded_proc(p_input VARCHAR2) AS
BEGIN
    NULL;
END;
/

CREATE OR REPLACE PROCEDURE overloaded_proc(p_input VARCHAR2, p_level NUMBER) AS
BEGIN
    NULL;
END;
/

-- Standalone procedure without overloading
CREATE OR REPLACE PROCEDURE standalone_proc(p_input VARCHAR2) AS
BEGIN
    NULL;
END;
/