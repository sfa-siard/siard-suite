package ch.admin.bar.siardsuite.localwebapp.connections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spec scenario "Store location per platform": the resolver picks the per-OS application-data
 * directory, and the directory is created user-only.
 */
class ConnectionPathsTest {

    private static final Path HOME = Path.of("/home/testuser");

    @Test
    void windowsUsesAppData() {
        Path dir = ConnectionPaths.appDataDirectory(
                "Windows 11", Map.of("APPDATA", "D:/Users/test/AppData/Roaming"), HOME);
        assertEquals(Path.of("D:/Users/test/AppData/Roaming").resolve("siard-suite-web"), dir);
    }

    @Test
    void windowsFallsBackToProfileAppData() {
        Path dir = ConnectionPaths.appDataDirectory("Windows 10", Map.of(), HOME);
        assertEquals(HOME.resolve("AppData").resolve("Roaming").resolve("siard-suite-web"), dir);
    }

    @Test
    void macOsUsesLibraryApplicationSupport() {
        Path dir = ConnectionPaths.appDataDirectory("Mac OS X", Map.of(), HOME);
        assertEquals(HOME.resolve("Library").resolve("Application Support").resolve("siard-suite-web"), dir);
    }

    @Test
    void linuxUsesXdgDataHome() {
        Path dir = ConnectionPaths.appDataDirectory(
                "Linux", Map.of("XDG_DATA_HOME", "/data/custom"), HOME);
        assertEquals(Path.of("/data/custom").resolve("siard-suite-web"), dir);
    }

    @Test
    void linuxFallsBackToDotLocalShare() {
        Path dir = ConnectionPaths.appDataDirectory("Linux", Map.of(), HOME);
        assertEquals(HOME.resolve(".local").resolve("share").resolve("siard-suite-web"), dir);
    }

    @Test
    void databaseFileIsNamedSiardSuiteDb(@TempDir Path tempDir) {
        Path file = ConnectionPaths.databaseFile("Linux", Map.of("XDG_DATA_HOME", tempDir.toString()), HOME);
        assertEquals(tempDir.resolve("siard-suite-web").resolve("siard-suite.db"), file);
    }

    @Test
    void createdDirectoryIsUserOnly(@TempDir Path tempDir) throws IOException {
        Path directory = tempDir.resolve("nested").resolve("siard-suite-web");
        ConnectionPaths.createUserOnlyDirectory(directory);

        assertTrue(Files.isDirectory(directory));
        if (FileSystems.getDefault().supportedFileAttributeViews().contains("posix")) {
            assertEquals(PosixFilePermissions.fromString("rwx------"),
                    Files.getPosixFilePermissions(directory));
        }
    }

    @Test
    void existingDirectoryKeepsItsPermissions(@TempDir Path tempDir) throws IOException {
        if (!FileSystems.getDefault().supportedFileAttributeViews().contains("posix")) {
            return;
        }
        Path shared = tempDir.resolve("shared");
        Files.createDirectory(shared);
        Files.setPosixFilePermissions(shared, PosixFilePermissions.fromString("rwxr-xr-x"));

        ConnectionPaths.createUserOnlyDirectory(shared);

        assertEquals(PosixFilePermissions.fromString("rwxr-xr-x"), Files.getPosixFilePermissions(shared));
    }
}
