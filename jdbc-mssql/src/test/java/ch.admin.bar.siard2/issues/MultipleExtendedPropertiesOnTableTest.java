package ch.admin.bar.siard2.issues;

import ch.admin.bar.siard2.jdbc.MsSqlConnection;
import ch.admin.bar.siard2.jdbc.MsSqlDatabaseMetaData;
import ch.admin.bar.siard2.jdbcx.MsSqlDataSource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MSSQLServerContainer;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

// Tests to reproduce https://github.com/sfa-siard/SiardGui/issues/50
public class MultipleExtendedPropertiesOnTableTest {

    private MsSqlDatabaseMetaData metaData;

    @Rule
    public MSSQLServerContainer mssqlserver = (MSSQLServerContainer) new MSSQLServerContainer()
            .acceptLicense()
            .withInitScript("scripts/mssql/multiple-extended-properties.sql");

    @Before
    public void setUp() throws Exception {
        MsSqlDataSource dataSource = new MsSqlDataSource(mssqlserver.getJdbcUrl(), mssqlserver.getUsername(), mssqlserver.getPassword());
        MsSqlConnection connection = (MsSqlConnection)dataSource.getConnection();

        metaData = (MsSqlDatabaseMetaData) connection.getMetaData();
    }

    @Test
    public void shouldConcatenateExtendedProperties() throws SQLException {
        // given
        ResultSet resultSet = metaData.getTables(null, "dbo", "%", new String[]{"TABLE"});

        // when
        resultSet.next();

        // then
        assertEquals("master", resultSet.getString(1));
        assertEquals("dbo", resultSet.getString(2));
        assertEquals("testtable", resultSet.getString(3));
        assertEquals("TABLE", resultSet.getString(4));
        assertEquals("Caption.| Description", resultSet.getString(5));
    }

    @Test
    public void shouldGetOnlyOneRow() throws SQLException {
        ResultSet resultSet = metaData.getTables(null, "dbo", "%", new String[]{"TABLE"});
        assertEquals(1, getNumberOfRows(resultSet));
    }

    private int getNumberOfRows(ResultSet resultSet) throws SQLException {
        int i = 0;
        while (resultSet.next()) i++;
        return i;
    }
}
