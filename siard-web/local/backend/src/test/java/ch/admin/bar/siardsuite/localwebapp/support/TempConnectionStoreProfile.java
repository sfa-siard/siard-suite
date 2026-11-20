package ch.admin.bar.siardsuite.localwebapp.support;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Points {@code siard.connections.db} at a fresh temp-dir database, so each test class gets an
 * isolated, empty connection store.
 */
public class TempConnectionStoreProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        try {
            Path directory = Files.createTempDirectory("siard-connections-test");
            return Map.of("siard.connections.db", directory.resolve("siard-suite.db").toString());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
