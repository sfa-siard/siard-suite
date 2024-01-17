package ch.admin.bar.siardsuite.database.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.sql.Connection;
import java.sql.SQLException;

@Value
public class DbmsConnectionData {
    Dbms<?> dbms;
    DbmsConnectionProperties<?> properties;

    public DbmsConnectionData(
            @NonNull ServerBasedDbms dbms,
            @NonNull ServerBasedDbmsConnectionProperties properties
    ) {
        this.dbms = dbms;
        this.properties = properties;
    }

    public DbmsConnectionData(
            @NonNull FileBasedDbms dbms,
            @NonNull FileBasedDbmsConnectionProperties properties
    ) {
        this.dbms = dbms;
        this.properties = properties;
    }


    public Connection createConnection() throws SQLException { // TODO: Move to connection factory

        throw new UnsupportedOperationException();
//        DriverManager.setLoginTimeout(Integer.parseInt(UserPreferences.node(OPTIONS).get(LOGIN_TIMEOUT.name(), "0")));
//        val connection = config.createConnection(dbms.getJdbcConnectionStringEncoder());
//
//        return connection;
    }
}
