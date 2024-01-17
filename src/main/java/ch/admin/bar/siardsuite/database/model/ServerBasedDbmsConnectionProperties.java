package ch.admin.bar.siardsuite.database.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Function;

@Value
@Builder
public class ServerBasedDbmsConnectionProperties implements DbmsConnectionProperties<ServerBasedDbmsConnectionProperties> {
    @NonNull String host;
    @NonNull String port;
    @NonNull String dbName;
    @NonNull String user;
    @NonNull String password;

    @Override
    public Connection createConnection(final Function<ServerBasedDbmsConnectionProperties, String> jdbcConnectionStringEncoder) throws SQLException {
        val jdbcConnectionString = jdbcConnectionStringEncoder.apply(this);
        return DriverManager.getConnection(
                jdbcConnectionString,
                user,
                password);
    }
}
