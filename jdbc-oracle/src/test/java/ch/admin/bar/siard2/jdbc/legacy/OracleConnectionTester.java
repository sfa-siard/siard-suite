package ch.admin.bar.siard2.jdbc.legacy;

import java.sql.*;

import static org.junit.Assert.*;

import ch.admin.bar.siard2.jdbc.OracleConnection;
import ch.admin.bar.siard2.oracle.legacy.TestOracleDatabase;
import ch.admin.bar.siard2.oracle.legacy.TestSqlDatabase;
import lombok.SneakyThrows;
import org.junit.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.jdbc.*;
import ch.admin.bar.siard2.jdbcx.*;
import org.testcontainers.containers.OracleContainer;

public class OracleConnectionTester extends BaseConnectionTester {
    private static final String _sDB_USER = "test";
    private static final String _sDB_PASSWORD = "test";

    private OracleConnection _connOracle = null;

    @ClassRule
    public final static OracleContainer db = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart");


    @BeforeClass
    public static void setUpClass() throws SQLException {
        OracleDataSource dsOracle = new OracleDataSource();
        dsOracle.setUrl(db.getJdbcUrl());
        dsOracle.setUser("SYSTEM");
        dsOracle.setPassword("test");
        OracleConnection connOracle = (OracleConnection) dsOracle.getConnection();

        new TestOracleDatabase(connOracle);
        TestOracleDatabase.grantSchema(connOracle, TestOracleDatabase._sTEST_SCHEMA, _sDB_USER);
        new TestSqlDatabase(connOracle);
        TestOracleDatabase.grantSchema(connOracle, TestSqlDatabase._sTEST_SCHEMA, _sDB_USER);
        connOracle.close();
    }

    @Before
    public void setUp() throws SQLException {
        OracleDataSource dsOracle = new OracleDataSource();
        dsOracle.setUrl(db.getJdbcUrl());
        dsOracle.setUser(_sDB_USER);
        dsOracle.setPassword(_sDB_PASSWORD);
        _connOracle = (OracleConnection) dsOracle.getConnection();
        _connOracle.setAutoCommit(false);
        setConnection(_connOracle);
    }

    @Test
    public void testClass() {
        assertEquals("Wrong connection class!", OracleConnection.class, _connOracle.getClass());
    }

    @Test
    @Override
    public void testAbort() {
    } // TODO: Fix this test

    @Test
    @Override
    public void testSetNetworkTimeout() {
    } // TODO: Fix this test

    @Test
    @Override
    @SneakyThrows
    public void testRollback() {
        _connOracle.setAutoCommit(false);
        _connOracle.rollback();
    }

    @Test
    @SneakyThrows
    public void testSetSavepoint() {
        _connOracle.setAutoCommit(false);
        _connOracle.setSavepoint();
    }

    @Test
    @SneakyThrows
    public void testSetSavepoint_String() {
        _connOracle.setAutoCommit(false);
        _connOracle.setSavepoint("TEST_SAVEPOINT");
    }

    @Test
    @SneakyThrows
    public void testRollback_Savepoint() {
        _connOracle.setAutoCommit(false);
        Savepoint sp = _connOracle.setSavepoint();
        _connOracle.rollback(sp);
    }

    @Test
    @SneakyThrows
    @Ignore("somehow stopped working after the try catch was removed")
    public void testReleaseSavePoint() {
        _connOracle.setAutoCommit(false);
        Savepoint sp = _connOracle.setSavepoint();
        _connOracle.releaseSavepoint(sp);
    }

    @Test
    @SneakyThrows
    public void testCreateStruct() {
        QualifiedId qiStructType = new QualifiedId(null, TestOracleDatabase._sTEST_SCHEMA, TestOracleDatabase._sUDT_OBJECT);
        _connOracle.createStruct(qiStructType.format(), new Object[]{"Label", Double.valueOf(3.14159)});
    }

    @Test
    @SneakyThrows
    public void testCreateArrayOf() {
        Array array = _connOracle.createArrayOf("VARCHAR(25)", new String[]{"a", "ab", "abc"});
        array.free();
    }

    @SneakyThrows
    @Test
    public void testCreateSqlXml() {
        SQLXML sqlxml = _connOracle.createSQLXML();
        sqlxml.free();
    }

    @Override
    @SneakyThrows
    public void testSetSchema() {
        // this._connOracle.setSchema("TEST");
    }
}
