package ch.admin.bar.siard2;

import ch.admin.bar.siard2.jdbcx.OracleDataSource;
import org.testcontainers.containers.JdbcDatabaseContainer;

import java.sql.SQLException;

public class OracleDatasourceFactory {

    public OracleDataSource create(JdbcDatabaseContainer container) throws SQLException {
        return new OracleDataSource(container.getJdbcUrl(), container.getUsername(), container.getPassword());
    }

    public OracleDataSource create(JdbcDatabaseContainer container, String username, String password) throws SQLException {
        return new OracleDataSource(container.getJdbcUrl(), username, password);
    }
}
