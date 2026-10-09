package ch.admin.bar.siard2.jdbcx;

import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MSSQLServerContainer;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.Assertions;

@Testcontainers
public class MSSQLDatasourceTest {
    private static final String MSSQL_IMAGE = "mcr.microsoft.com/mssql/server:2022-latest";
    private static final String SA_PASSWORD = "YourStrong!Passw0rd";

    @Container
    public static MSSQLServerContainer<?> mssqlContainer = new MSSQLServerContainer<>(MSSQL_IMAGE)
            .acceptLicense()
            .withPassword(SA_PASSWORD)
            .withUrlParam("trustServerCertificate", "true");

    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;

    private MsSqlDataSource dataSource = null;
    private Connection connection = null;

    @BeforeAll
    public static void setUpClass() {
        DB_URL = mssqlContainer.getJdbcUrl();
        DB_USER = mssqlContainer.getUsername();
        DB_PASSWORD = mssqlContainer.getPassword();
    }

    @BeforeEach
    public void setUp() {
        dataSource = new MsSqlDataSource();
    }

    @AfterEach
    @SneakyThrows
    public void tearDown() {
        if ((connection != null) && (!connection.isClosed()))
            connection.close();
    }

    @Test
    @SneakyThrows
    public void testWrapper() {
        Assertions.assertSame(true, dataSource.isWrapperFor(DataSource.class), "Invalid wrapper!");
        DataSource dsWrapped = dataSource.unwrap(DataSource.class);
        assertSame(com.microsoft.sqlserver.jdbc.SQLServerDataSource.class, dsWrapped.getClass(), "Invalid wrapped class!");
    }

    @Test
    @SneakyThrows
    public void testLoginTimeout() {
        assertTrue(dataSource.getLoginTimeout() > 0);
    }

    @Test
    @SneakyThrows
    public void testConnection() {
        dataSource.setUrl(DB_URL);
        dataSource.setUser(DB_USER);
        dataSource.setPassword(DB_PASSWORD);
        connection = dataSource.getConnection();
    }

}
