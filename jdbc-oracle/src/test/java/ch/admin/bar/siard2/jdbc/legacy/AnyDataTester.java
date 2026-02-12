package ch.admin.bar.siard2.jdbc.legacy;

import ch.admin.bar.siard2.jdbcx.OracleDataSource;
import ch.enterag.utils.jdbc.BaseDatabaseMetaDataTester;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.OracleContainer;

import java.sql.*;

public class AnyDataTester {
    private Connection _conn;

    @Rule
    public final OracleContainer db = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart");


    @Before
    public void setUp() throws SQLException {
        OracleDataSource dsOracle = new OracleDataSource();
        dsOracle.setUrl(db.getJdbcUrl());
        dsOracle.setUser(db.getUsername());
        dsOracle.setPassword(db.getPassword());
        _conn = dsOracle.getConnection();
        _conn.setAutoCommit(false);
    }


    @Test
    public void testGetColumns() throws SQLException {
        DatabaseMetaData dmd = _conn.getMetaData();
        ResultSet rs = dmd.getColumns(null, db.getUsername(), "IFS_IN_TABLE", "%");
        BaseDatabaseMetaDataTester.print(rs);
    }

    @Test
    @Ignore("seems to depend on setup @enterag")
    public void testGetObject() throws SQLException {
        Statement stmt = _conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT USER_PROP FROM IFS_IN_TABLE");
        while (rs.next()) {
            Object o = rs.getObject(1);
            System.out.println(String.valueOf(o));
        }
    }

    @Test
    @Ignore("seems to depend on setup @enterag")
    public void testGetClob() throws SQLException {
        Statement stmt = _conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT USER_PROP FROM IFS_IN_TABLE");
        while (rs.next()) {
            Clob clob = rs.getClob(1);
            System.out.println(clob.getSubString(1, (int) clob.length()));
        }
    }
}
