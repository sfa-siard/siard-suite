package ch.admin.bar.siardsuite.localwebapp.connections;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Resolves where the saved-connection store lives (design D4): the {@code siard.connections.db}
 * config value wins; otherwise the per-OS application-data directory, created user-only.
 */
public final class ConnectionPaths {

    public static final String APP_DIRECTORY = "siard-suite-web";
    public static final String DATABASE_FILE = "siard-suite.db";

    private static final String OWNER_ONLY_DIRECTORY = "rwx------";
    private static final String OWNER_ONLY_FILE = "rw-------";

    private ConnectionPaths() {
    }

    /**
     * The application-data directory for the given platform facts: {@code %APPDATA%} on Windows
     * (falling back to the profile's {@code AppData\Roaming}), {@code ~/Library/Application
     * Support} on macOS and {@code $XDG_DATA_HOME} or {@code ~/.local/share} elsewhere.
     */
    public static Path appDataDirectory(String osName, Map<String, String> environment, Path userHome) {
        String os = osName.toLowerCase(Locale.ROOT);
        if (os.startsWith("windows")) {
            String appData = environment.get("APPDATA");
            if (appData != null && !appData.isBlank()) {
                return Path.of(appData).resolve(APP_DIRECTORY);
            }
            return userHome.resolve("AppData").resolve("Roaming").resolve(APP_DIRECTORY);
        }
        if (os.contains("mac")) {
            return userHome.resolve("Library").resolve("Application Support").resolve(APP_DIRECTORY);
        }
        String xdgDataHome = environment.get("XDG_DATA_HOME");
        Path base = xdgDataHome != null && !xdgDataHome.isBlank()
                ? Path.of(xdgDataHome)
                : userHome.resolve(".local").resolve("share");
        return base.resolve(APP_DIRECTORY);
    }

    /** The SQLite file inside {@link #appDataDirectory}: {@code siard-suite.db}. */
    public static Path databaseFile(String osName, Map<String, String> environment, Path userHome) {
        return appDataDirectory(osName, environment, userHome).resolve(DATABASE_FILE);
    }

    /**
     * Creates the directory (and parents) and restricts it to the current user where POSIX
     * applies. An existing directory is left untouched: a configured database path may point
     * into a shared or working directory whose permissions are not ours to change.
     */
    public static void createUserOnlyDirectory(Path directory) throws IOException {
        if (Files.isDirectory(directory)) {
            return;
        }
        Files.createDirectories(directory);
        setPosixPermissions(directory, PosixFilePermissions.fromString(OWNER_ONLY_DIRECTORY));
    }

    /** Restricts an existing file to the current user where POSIX applies; no-op elsewhere. */
    public static void restrictFileToOwner(Path file) throws IOException {
        setPosixPermissions(file, PosixFilePermissions.fromString(OWNER_ONLY_FILE));
    }

    private static void setPosixPermissions(Path path, Set<PosixFilePermission> permissions) throws IOException {
        if (FileSystems.getDefault().supportedFileAttributeViews().contains("posix")) {
            Files.setPosixFilePermissions(path, permissions);
        }
    }
}
