package ch.admin.bar.siardsuite.localwebapp.database;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers the ported {@link DbmsRegistry}: the six DBMS entries, the server-based subset and the
 * JDBC URL encoding of PostgreSQL and MS Access.
 */
class DbmsRegistryTest {

    @Test
    void registersSixDbms() {
        assertEquals(6, DbmsRegistry.DBMS.size());
    }

    @Test
    void fiveDbmsAreServerBased() {
        assertEquals(5, DbmsRegistry.serverBased().size());
        assertTrue(DbmsRegistry.DBMS.stream().allMatch(dbms ->
                dbms instanceof ServerBasedDbms || dbms instanceof FileBasedDbms));
    }

    @Test
    void postgreSqlEncoderProducesJdbcUrl() {
        ServerBasedDbms postgresql = serverBased("postgresql");
        ServerBasedDbmsConnectionProperties properties = new ServerBasedDbmsConnectionProperties(
                "host", "port", "db", "user", "password", Optional.of("options"));

        assertEquals("jdbc:postgresql://host:port/db?options",
                postgresql.jdbcConnectionStringEncoder().apply(properties));
    }

    @Test
    void postgreSqlDecoderRoundTrips() {
        ServerBasedDbms postgresql = serverBased("postgresql");
        ServerBasedDbmsConnectionProperties properties = new ServerBasedDbmsConnectionProperties(
                "host", "5432", "db", "", "", Optional.of("ssl=true"));

        ServerBasedDbmsConnectionProperties decoded =
                postgresql.jdbcConnectionStringDecoder()
                        .apply(postgresql.jdbcConnectionStringEncoder().apply(properties));

        assertEquals(properties.host(), decoded.host());
        assertEquals(properties.port(), decoded.port());
        assertEquals(properties.dbName(), decoded.dbName());
        assertEquals(Optional.of("ssl=true"), decoded.options());
    }

    @Test
    void msAccessUsesJdbcAccessEncoding() {
        FileBasedDbms access = assertInstanceOf(FileBasedDbms.class,
                DbmsRegistry.findById("access").orElseThrow());

        String url = access.jdbcConnectionStringEncoder()
                .apply(new FileBasedDbmsConnectionProperties(Path.of("testfiles", "dbfile.mdb")));

        assertTrue(url.startsWith("jdbc:access:"), url);
        assertTrue(url.endsWith("dbfile.mdb"), url);
    }

    private static ServerBasedDbms serverBased(String id) {
        return assertInstanceOf(ServerBasedDbms.class, DbmsRegistry.findById(id).orElseThrow());
    }
}
