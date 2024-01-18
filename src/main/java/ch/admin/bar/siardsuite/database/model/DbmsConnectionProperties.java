package ch.admin.bar.siardsuite.database.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

public interface DbmsConnectionProperties<T extends DbmsConnectionProperties> {
    Connection createConnection(final Function<T, String> jdbcConnectionStringEncoder) throws SQLException;
}
