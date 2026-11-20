package ch.admin.bar.siardsuite.localwebapp.connections;

import ch.admin.bar.siardsuite.localwebapp.support.TempConnectionStoreProfile;
import io.quarkus.arc.Arc;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The store is a CDI bean whose schema is initialized at startup: on a fresh profile the database
 * file already exists after boot and the store lists no connections. The HTTP-level check
 * ({@code GET /api/connections} → 200 {@code []}) arrives with the connections resource.
 */
@QuarkusTest
@TestProfile(TempConnectionStoreProfile.class)
class ConnectionStoreBootTest {

    @ConfigProperty(name = "siard.connections.db")
    String databaseFile;

    @Test
    void storeIsInitializedAtStartup() {
        // no store call happened yet in this test: the file exists because startup created it
        assertTrue(Files.isRegularFile(Path.of(databaseFile)),
                "the SQLite file is created at startup");

        ConnectionStore store = Arc.container().instance(ConnectionStore.class).get();
        assertTrue(store.list().isEmpty(), "a fresh profile has no saved connections");
    }

    @Test
    void storeBeanIsTheSqliteImplementation() {
        assertTrue(Arc.container().instance(SqliteConnectionStore.class).isAvailable());
        assertEquals(0, Arc.container().instance(ConnectionStore.class).get().list().size());
    }
}
