package ch.admin.bar.siardsuite.localwebapp.database;

import java.util.function.Function;

/**
 * A database management system the archive workflow can connect to: its identity, JDBC driver
 * and the functions translating connection properties to and from a JDBC URL.
 * <p>
 * Ported from {@code siard-suite-app}'s {@code service.database.model}, which is frozen; the two
 * copies may drift until the model moves to {@code backend-core} (design D1).
 */
public interface Dbms<T extends DbmsConnectionProperties> {

    String name();

    String id();

    String driverClassName();

    Function<T, String> jdbcConnectionStringEncoder();

    Function<String, T> jdbcConnectionStringDecoder();
}
