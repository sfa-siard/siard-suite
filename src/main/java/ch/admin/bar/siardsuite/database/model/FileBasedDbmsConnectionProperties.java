package ch.admin.bar.siardsuite.database.model;

import lombok.NonNull;
import lombok.Value;
import lombok.val;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Function;

@Value
public class FileBasedDbmsConnectionProperties implements DbmsConnectionProperties<FileBasedDbmsConnectionProperties> {
    @NonNull File file;

    public Connection createConnection(final Function<FileBasedDbmsConnectionProperties, String> jdbcConnectionStringEncoder) throws SQLException {
        val jdbcConnectionString = jdbcConnectionStringEncoder.apply(this);
        return DriverManager.getConnection(jdbcConnectionString);
    }
}
