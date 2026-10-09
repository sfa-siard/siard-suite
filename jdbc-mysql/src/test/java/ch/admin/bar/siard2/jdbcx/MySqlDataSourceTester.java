package ch.admin.bar.siard2.jdbcx;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.MountableFile;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Assertions;

public class MySqlDataSourceTester {
    private static final MySQLContainer<?> _mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testschema")
            .withUsername("testuser")
            .withPassword("testpwd")
            .withCopyFileToContainer(MountableFile.forClasspathResource("zzz-test-overrides.cnf"), "/etc/mysql/conf.d/zzz-test-overrides.cnf");

    private static String _sDB_URL;
    private static String _sDB_USER;
    private static String _sDB_PASSWORD;

    @BeforeAll
    public static void setUpClass() {
        _mysql.start();
        _sDB_URL = "jdbc:mysql://" + _mysql.getHost() + ":" + _mysql.getFirstMappedPort();
        _sDB_USER = _mysql.getUsername();
        _sDB_PASSWORD = _mysql.getPassword();
    }

    @AfterAll
    public static void tearDownClass() {
        _mysql.stop();
    }

    private MySqlDataSource _dsMySql = null;
    private Connection _conn = null;

    @BeforeEach
    public void setUp() throws Exception {
        _dsMySql = new MySqlDataSource();
    }

    @AfterEach
    public void tearDown() throws Exception {
        if ((_conn != null) && (!_conn.isClosed())) {
            _conn.close();
        }
    }

    @Test
    public void testWrapper() {
        try {
            Assertions.assertSame(true, _dsMySql.isWrapperFor(DataSource.class), "Invalid wrapper!");
            DataSource dsWrapped = _dsMySql.unwrap(DataSource.class);
            assertSame(com.mysql.cj.jdbc.MysqlDataSource.class, dsWrapped.getClass(), "Invalid wrapper class!");
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }

    @Test
    public void testConnection() {
        _dsMySql.setUrl(_sDB_URL);
        _dsMySql.setUser(_sDB_USER);
        _dsMySql.setPassword(_sDB_PASSWORD);

        try {
            _conn = _dsMySql.getConnection();
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }

    @Test
    public void testLoginTimeout() {
        try {
            int iLoginTimeout = _dsMySql.getLoginTimeout();
            assertSame(iLoginTimeout, 0, "Unexpected login timeout " + String.valueOf(iLoginTimeout) + "!");
        } catch (SQLException se) {
            fail(se.getClass()
                   .getName() + ": " + se.getMessage());
        }
    }

}
