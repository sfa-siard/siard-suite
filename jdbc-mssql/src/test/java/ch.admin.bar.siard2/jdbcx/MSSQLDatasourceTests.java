package ch.admin.bar.siard2.jdbcx;

import lombok.SneakyThrows;
import org.junit.*;
import org.testcontainers.containers.MSSQLServerContainer;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.Assert.assertSame;

public class MSSQLDatasourceTests {
    private static final String MSSQL_IMAGE = "mcr.microsoft.com/mssql/server:2022-latest";
    private static final String SA_PASSWORD = "YourStrong!Passw0rd";

    @ClassRule
    public static MSSQLServerContainer<?> mssqlContainer = new MSSQLServerContainer<>(MSSQL_IMAGE)
            .acceptLicense()
            .withPassword(SA_PASSWORD);

    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;

    private MsSqlDataSource dataSource = null;
    private Connection connection = null;

    @BeforeClass
    public static void setUpClass() {
        DB_URL = "jdbc:sqlserver://" + mssqlContainer.getHost() + ":" + mssqlContainer.getMappedPort(1433);
        DB_USER = mssqlContainer.getUsername();
        DB_PASSWORD = mssqlContainer.getPassword();
    }

    @Before
    public void setUp() {
        dataSource = new MsSqlDataSource();
    }

    @After
    @SneakyThrows
    public void tearDown() {
        if ((connection != null) && (!connection.isClosed()))
            connection.close();
    }

    @Test
    @SneakyThrows
    public void testWrapper() {
        Assert.assertSame("Invalid wrapper!", true, dataSource.isWrapperFor(DataSource.class));
        DataSource dsWrapped = dataSource.unwrap(DataSource.class);
        assertSame("Invalid wrapped class!", com.microsoft.sqlserver.jdbc.SQLServerDataSource.class, dsWrapped.getClass());
    }

    @Test
    @SneakyThrows
    public void testLoginTimeout() {
        int iLoginTimeout = dataSource.getLoginTimeout();
        assertSame("Unexpected login timeout " + String.valueOf(iLoginTimeout) + "!", 15, iLoginTimeout);
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
