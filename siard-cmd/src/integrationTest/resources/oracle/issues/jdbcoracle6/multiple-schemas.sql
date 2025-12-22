ALTER SESSION SET CONTAINER=XEPDB1;

CREATE USER testuser IDENTIFIED BY testpassword;
CREATE USER otheruser IDENTIFIED BY otherpassword;

GRANT CREATE SESSION, CREATE TABLE, CREATE TYPE, CREATE PROCEDURE TO testuser;
GRANT CREATE SESSION, CREATE TABLE, CREATE TYPE, CREATE PROCEDURE TO otheruser;

ALTER SESSION SET CURRENT_SCHEMA = otheruser;

CREATE TABLE simple_table (
                              id NUMBER,
                              value VARCHAR2(100)
);

ALTER SESSION SET CURRENT_SCHEMA = testuser;

CREATE TABLE simple_table (
                              id NUMBER,
                              value VARCHAR2(100)
);

-- the following grant is the reason for the expected exception in ch.admin.bar.siard2.cmd.oracle.issues.jdbcoracle6.MultipleSchemasIT.download
-- the testuser has no permission to read otheruser.simple_table, causing the download to fail with the following error:
-- ORA-01031: insufficient privileges
GRANT INSERT ON otheruser.simple_table TO testuser;
