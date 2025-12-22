package ch.admin.bar.siard2.jdbc.legacy;

import ch.admin.bar.siard2.jdbc.OracleConnection;
import ch.admin.bar.siard2.jdbc.OracleDatabaseMetaData;
import ch.admin.bar.siard2.jdbc.OracleResultSet;
import ch.admin.bar.siard2.jdbcx.OracleDataSource;
import ch.enterag.utils.jdbc.BaseDatabaseMetaDataTester;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.testcontainers.containers.OracleContainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OracleLongTester extends BaseDatabaseMetaDataTester {

    private static OracleDatabaseMetaData _dmdOracle = null;

    @ClassRule
    public final static OracleContainer db = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart");

    @Before
    public void beforeClass() throws SQLException {
        OracleDataSource dsOracle = new OracleDataSource();
        dsOracle.setUrl(db.getJdbcUrl());
        dsOracle.setUser(db.getUsername());
        dsOracle.setPassword(db.getPassword());

        OracleConnection connOracle = (OracleConnection) dsOracle.getConnection();
        connOracle.setAutoCommit(false);
        _dmdOracle = (OracleDatabaseMetaData) connOracle.getMetaData();
        setDatabaseMetaData(_dmdOracle);
    }


    private void listLengths(String sSql, String sColumnLabel) throws SQLException {
        /* if this is called AFTER the ResultSet has been gotten, then the
         * internal stream of the ResultSet is closed! */
        Connection conn = _dmdOracle.getConnection();
        PreparedStatement pstmt = _dmdOracle.getConnection()
                                            .unwrap(Connection.class)
                                            .prepareStatement(sSql);
        ResultSet rs = pstmt.executeQuery();
        rs = new OracleResultSet(rs, conn, pstmt);
        while (rs.next()) {
            String s = rs.getString(sColumnLabel);
            System.out.println(String.valueOf(s.length()));
        }
        rs.close();
    }

    @Test
    @Ignore
    public void testAllViews() throws SQLException {
        String sSql = "SELECT * FROM ALL_VIEWS";
        listLengths(sSql, "TEXT");
    }

    @Test
    public void testGetViews() throws SQLException {
        String sSql = "SELECT\r\n" + "  NULL AS TABLE_CAT,\r\n" + "  O.OWNER AS TABLE_SCHEM,\r\n" + "  O.OBJECT_NAME AS TABLE_NAME,\r\n" + "  CASE\r\n" + "  WHEN O.OBJECT_TYPE = 'TABLE' AND O.TEMPORARY = 'N' AND O.ORACLE_MAINTAINED = 'N' THEN 'TABLE'\r\n" + "  WHEN O.OBJECT_TYPE = 'TABLE' AND O.TEMPORARY = 'Y' AND O.ORACLE_MAINTAINED = 'N' THEN 'GLOBAL TEMPORARY'\r\n" + "  WHEN O.OBJECT_TYPE = 'TABLE' AND O.TEMPORARY = 'N' AND O.ORACLE_MAINTAINED = 'Y' THEN 'SYSTEM TABLE'\r\n" + "  WHEN O.OBJECT_TYPE = 'VIEW' AND O.TEMPORARY = 'N' AND O.ORACLE_MAINTAINED = 'N' THEN 'VIEW'\r\n" + "  WHEN O.OBJECT_TYPE = 'SYNONYM' AND O.TEMPORARY = 'N' AND O.ORACLE_MAINTAINED = 'N' THEN 'SYNONYM'\r\n" + "  ELSE NULL END AS TABLE_TYPE,\r\n" + "  TC.COMMENTS AS REMARKS,\r\n" + "  NULL AS TYPE_CAT,\r\n" + "  NULL AS TYPE_SCHEM,\r\n" + "  NULL AS TYPE_NAME,\r\n" + "  NULL AS SELF_REFERENCING_COL_NAME,\r\n" + "  NULL AS REF_GENERATION,\r\n" + "  V.TEXT AS QUERY_TEXT\r\n" + "  FROM ALL_OBJECTS O\r\n" + "  LEFT JOIN ALL_TABLES T\r\n" + "  ON (O.OWNER = T.OWNER AND\r\n" + "  O.OBJECT_NAME = T.TABLE_NAME AND\r\n" + "  O.OBJECT_TYPE = 'TABLE' AND\r\n" + "  T.NESTED = 'NO')\r\n" + "  LEFT JOIN ALL_VIEWS V\r\n" + "  ON (O.OWNER = V.OWNER AND\r\n" + "  O.OBJECT_NAME = V.VIEW_NAME AND\r\n" + "  O.OBJECT_TYPE = 'VIEW')\r\n" + "  LEFT JOIN ALL_TAB_COMMENTS TC\r\n" + "  ON (O.OWNER = TC.OWNER AND\r\n" + "  O.OBJECT_NAME = TC.TABLE_NAME AND\r\n" + "  O.OBJECT_TYPE = TC.TABLE_TYPE)\r\n" + "  WHERE\r\n" + "  O.OBJECT_NAME LIKE '%' ESCAPE '/'\r\n" + "  AND O.OWNER LIKE 'TESTSQLUSER' ESCAPE '/'\r\n" + "  AND (\r\n" + "  (O.OBJECT_TYPE = 'VIEW' AND NOT V.VIEW_NAME IS NULL)\r\n" + "  )\r\n" + "  AND O.ORACLE_MAINTAINED = 'N'\r\n" + "  AND O.GENERATED = 'N'\r\n" + "  ORDER BY TABLE_TYPE, TABLE_CAT, TABLE_SCHEM, TABLE_NAME";
        listLengths(sSql, "QUERY_TEXT");
    }

    @SneakyThrows
    @Override
    public void testGetColumns() {
        print(this._dmdOracle.getColumns(null, null, "%", "%"));
    }


    @SneakyThrows
    @Override
    @Ignore
    public void testGetAttributes() {
        // print(this._dmdOracle.getAttributes(null, null, "%", "%"));
    }
}