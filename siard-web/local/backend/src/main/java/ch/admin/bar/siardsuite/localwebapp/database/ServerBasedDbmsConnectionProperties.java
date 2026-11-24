package ch.admin.bar.siardsuite.localwebapp.database;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Connection properties of a server-based DBMS: host, port, database, credentials and free-form
 * JDBC options.
 */
public record ServerBasedDbmsConnectionProperties(
        String host,
        String port,
        String dbName,
        String user,
        String schema,
        String password,
        Optional<String> options) implements DbmsConnectionProperties {

    public ServerBasedDbmsConnectionProperties {
        requireNonNull(host, "host");
        requireNonNull(port, "port");
        requireNonNull(dbName, "dbName");
        requireNonNull(user, "user");
        requireNonNull(password, "password");
        schema = schema == null ? "%" : schema;
        options = options == null ? Optional.empty() : options;
    }

    public ServerBasedDbmsConnectionProperties(
            String host, String port, String dbName, String user, String password) {
        this(host, port, dbName, user, "%", password, Optional.empty());
    }

    public ServerBasedDbmsConnectionProperties(
            String host, String port, String dbName, String user, String password, Optional<String> options) {
        this(host, port, dbName, user, "%", password, options);
    }

    // the password is a credential: never render it, like @ToString.Exclude in the desktop copy
    @Override
    public String toString() {
        return "ServerBasedDbmsConnectionProperties[host=" + host + ", port=" + port + ", dbName=" + dbName
                + ", user=" + user + ", schema=" + schema + ", password=***, options=" + options + "]";
    }
}
