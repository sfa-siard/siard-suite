ALTER SESSION SET CONTAINER=XEPDB1;

-- OracleConnectionTesters needs a schema "TEST_SCHEMA" - create the user (oracle treats users as schemas?)
CREATE USER TEST_SCHEMA identified by testschemapwd;

-- specific user for ch.admin.bar.siard2.jdbc.OracleDatabaseMetaDataTester.testGetOeTables and ch.admin.bar.siard2.jdbc.OracleDatabaseMetaDataTester.testGetOeTables
CREATE USER OE identified by OEPWD;
GRANT CONNECT, RESOURCE TO OE;


-- specific undocumented user "buguser" used in ch.admin.bar.siard2.jdbc.OracleStatementTester.testExecuteBug and ch.admin.bar.siard2.jdbc.OracleStatementTester.testDownloadLongRaw
CREATE USER buguser identified by bugpwd;
GRANT CONNECT, RESOURCE TO buguser;
GRANT UNLIMITED TABLESPACE TO buguser;