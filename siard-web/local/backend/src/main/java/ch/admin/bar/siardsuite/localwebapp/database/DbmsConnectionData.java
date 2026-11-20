package ch.admin.bar.siardsuite.localwebapp.database;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A DBMS together with concrete connection properties; derives the JDBC URL, database name and
 * credentials on demand. The suppliers are excluded from equality, like in the desktop copy.
 */
public final class DbmsConnectionData {

    private final Dbms<?> dbms;
    private final DbmsConnectionProperties properties;
    private final Supplier<String> jdbcConnectionString;
    private final Supplier<String> user;
    private final Supplier<String> password;
    private final Supplier<String> dbName;

    private DbmsConnectionData(
            Dbms<?> dbms,
            DbmsConnectionProperties properties,
            Supplier<String> jdbcConnectionString,
            Supplier<String> user,
            Supplier<String> password,
            Supplier<String> dbName) {
        this.dbms = dbms;
        this.properties = properties;
        this.jdbcConnectionString = jdbcConnectionString;
        this.user = user;
        this.password = password;
        this.dbName = dbName;
    }

    public static DbmsConnectionData of(ServerBasedDbms dbms, ServerBasedDbmsConnectionProperties properties) {
        return new DbmsConnectionData(
                dbms,
                properties,
                () -> dbms.jdbcConnectionStringEncoder().apply(properties),
                properties::user,
                properties::password,
                properties::dbName);
    }

    public static DbmsConnectionData of(FileBasedDbms dbms, FileBasedDbmsConnectionProperties properties) {
        return new DbmsConnectionData(
                dbms,
                properties,
                () -> dbms.jdbcConnectionStringEncoder().apply(properties),
                () -> null,
                () -> null,
                () -> filenameWithoutExtension(properties.file()));
    }

    private static String filenameWithoutExtension(Path file) {
        String name = file.getFileName().toString();
        int lastDot = name.lastIndexOf('.');
        return lastDot > 0 ? name.substring(0, lastDot) : name;
    }

    public Dbms<?> dbms() {
        return dbms;
    }

    public DbmsConnectionProperties properties() {
        return properties;
    }

    public String user() {
        return user.get();
    }

    public String password() {
        return password.get();
    }

    public String jdbcConnectionString() {
        return jdbcConnectionString.get();
    }

    public String dbName() {
        return dbName.get();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DbmsConnectionData that)) {
            return false;
        }
        return dbms.equals(that.dbms) && properties.equals(that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dbms, properties);
    }

    @Override
    public String toString() {
        // never include the suppliers: they can resolve to credentials
        return "DbmsConnectionData[dbms=" + dbms.id() + ", properties=" + properties + "]";
    }
}
