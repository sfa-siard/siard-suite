package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.jdbcx.MySqlDataSource;
import ch.admin.bar.siard2.mysql.TestMySqlDatabase;
import ch.admin.bar.siard2.mysql.TestSqlDatabase;
import ch.enterag.utils.EU;
import ch.enterag.utils.jdbc.BaseConnectionTester;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.MountableFile;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class MySqlConnectionTester extends BaseConnectionTester {
    private static final MySQLContainer<?> _mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testschema")
            .withUsername("testuser")
            .withPassword("testpwd")
            .withCopyFileToContainer(MountableFile.forClasspathResource("zzz-test-overrides.cnf"), "/etc/mysql/conf.d/zzz-test-overrides.cnf");

    private static String _sDB_URL;
    private static String _sDB_USER;
    private static String _sDB_PASSWORD;
    private static String _sDB_CATALOG;
    private static String _sDBA_USER;
    private static String _sDBA_PASSWORD;

    private MySqlConnection _connMySql = null;

    @BeforeAll
    public static void setUpClass() {
        try {
            _mysql.start();
            _sDB_CATALOG = _mysql.getDatabaseName();
            _sDB_URL = MySqlDriver.getUrl(_mysql.getHost() + ":" + _mysql.getFirstMappedPort() + "/" + _sDB_CATALOG, true);
            _sDB_USER = _mysql.getUsername();
            _sDB_PASSWORD = _mysql.getPassword();
            _sDBA_USER = "root";
            _sDBA_PASSWORD = _mysql.getPassword();
            MySqlDataSource dsMySql = new MySqlDataSource();
            dsMySql.setUrl(_sDB_URL);
            dsMySql.setUser(_sDBA_USER);
            dsMySql.setPassword(_sDBA_PASSWORD);
            MySqlConnection connMySql = (MySqlConnection) dsMySql.getConnection();
            new TestMySqlDatabase(connMySql);
            TestMySqlDatabase.grantSchemaUser(connMySql,
                                              TestMySqlDatabase._sTEST_SCHEMA, _sDB_USER);
            new TestSqlDatabase(connMySql);
            TestMySqlDatabase.grantSchemaUser(connMySql,
                                              TestSqlDatabase._sTEST_SCHEMA, _sDB_USER);
            connMySql.close();
        } catch (SQLException se) {
            se.printStackTrace();
            fail(EU.getExceptionMessage(se));
        }
    }

    @AfterAll
    public static void tearDownClass() {
        _mysql.stop();
    }

    @BeforeEach
    public void setUp() {
        try {
            MySqlDataSource dsMySql = new MySqlDataSource();
            dsMySql.setUrl(_sDB_URL);
            dsMySql.setUser(_sDB_USER);
            dsMySql.setPassword(_sDB_PASSWORD);
            _connMySql = (MySqlConnection) dsMySql.getConnection();
            _connMySql.setAutoCommit(false);
            setConnection(_connMySql);
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testClass() {
        assertEquals(MySqlConnection.class, _connMySql.getClass(), "Wrong connection class!");
    }

    @Test
    @Override
    public void testGetMetadata() {
        enter();
        try {
            DatabaseMetaData dmd = _connMySql.getMetaData();
            assertEquals(MySqlDatabaseMetaData.class, dmd.getClass(), "Wrong metadata class!");
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    @Override
    public void testSetCatalog() {
        try {
            _connMySql.setCatalog(_sDB_CATALOG);
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

}
