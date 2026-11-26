package ch.admin.bar.siardsuite.localwebapp.files;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import ch.admin.bar.siardsuite.webcore.error.ProblemException;

/** The per-OS commands of design D5, asserted without starting any process. */
class FileManagerLauncherTest {

    private static final Path FILE = Path.of("/home/me/My Data/db.siard");
    private static final Path FOLDER = Path.of("/home/me/My Data");
    private static final Map<String, String> WINDOWS_ENV = Map.of("SystemRoot", "C:\\WINDOWS");

    private final List<List<String>> started = new ArrayList<>();

    // --- Windows ---

    @Test
    void windowsStartsExplorerByFullPathFromSystemRoot() {
        launcher(Os.WINDOWS, WINDOWS_ENV, command -> exited(1)).reveal(Path.of("C:\\Users\\me\\My Data\\db.siard"), false);
        launcher(Os.WINDOWS, WINDOWS_ENV, command -> exited(1)).reveal(Path.of("C:\\Users\\me\\My Data"), true);

        // Explorer's exit code 1 is not a failure
        assertEquals(List.of(
                List.of("C:\\WINDOWS\\explorer.exe", "/select,C:\\Users\\me\\My Data\\db.siard"),
                List.of("C:\\WINDOWS\\explorer.exe", "C:\\Users\\me\\My Data")), started);
    }

    @Test
    void windowsFallsBackToWindirThenTheDefaultFolder() {
        launcher(Os.WINDOWS, Map.of("WINDIR", "D:\\Win"), command -> exited(0)).reveal(Path.of("C:\\a"), true);
        launcher(Os.WINDOWS, Map.of(), command -> exited(0)).reveal(Path.of("C:\\a"), true);

        assertEquals(List.of(
                List.of("D:\\Win\\explorer.exe", "C:\\a"),
                List.of("C:\\Windows\\explorer.exe", "C:\\a")), started);
    }

    // --- macOS ---

    @Test
    void macRevealsFileOrOpensFolderInFinder() {
        launcher(Os.MAC, Map.of(), command -> exited(0)).reveal(FILE, false);
        launcher(Os.MAC, Map.of(), command -> exited(0)).reveal(FOLDER, true);

        assertEquals(List.of(
                List.of("open", "-R", FILE.toString()),
                List.of("open", FOLDER.toString())), started);
    }

    @Test
    void macNonZeroExitIs503() {
        FileManagerLauncher launcher = launcher(Os.MAC, Map.of(), command -> exited(1));

        assertEquals(503, assertThrows(ProblemException.class, () -> launcher.reveal(FILE, false)).status());
    }

    // --- Linux ---

    @Test
    void linuxShowsItemOverDbus() {
        launcher(Os.LINUX, Map.of(), command -> exited(0)).reveal(FILE, false);

        assertEquals(List.of(dbusShowItems(FILE.toUri().toString())), started);
    }

    @Test
    void linuxQuoteInPathCannotEndTheDbusArgument() {
        Path quoted = Path.of("/home/me/it's.siard");
        launcher(Os.LINUX, Map.of(), command -> exited(0)).reveal(quoted, false);

        // the URI is built like the expectation on every OS (on Windows /home/... becomes C:/home/...)
        String uri = started.get(0).get(started.get(0).size() - 2);
        assertEquals("['" + quoted.toUri().toString().replace("'", "%27") + "']", uri);
        assertEquals(-1, uri.substring(2, uri.length() - 2).indexOf('\''), uri);
    }

    @Test
    void linuxFallsBackToOpeningTheParentFolder() {
        Function<List<String>, Process> dbusFails = command -> command.get(0).equals("gdbus") ? exited(1) : exited(0);
        FakeProcess hangingDbus = new FakeProcess(0, false);
        Function<List<String>, Process> dbusHangs = command -> command.get(0).equals("gdbus") ? hangingDbus : exited(0);

        launcher(Os.LINUX, Map.of(), dbusFails).reveal(FILE, false);
        launcher(Os.LINUX, Map.of(), dbusHangs).reveal(FILE, false);

        assertEquals(List.of(
                dbusShowItems(FILE.toUri().toString()),
                List.of("xdg-open", FOLDER.toString()),
                dbusShowItems(FILE.toUri().toString()),
                List.of("xdg-open", FOLDER.toString())), started);
        assertTrue(hangingDbus.destroyed, "a hanging gdbus is stopped");
    }

    @Test
    void linuxShowsFolderOverDbus() {
        launcher(Os.LINUX, Map.of(), command -> exited(0)).reveal(FOLDER, true);

        assertEquals(List.of(dbus("ShowFolders", FOLDER.toUri().toString())), started);
    }

    @Test
    void linuxFolderFallsBackToXdgOpen() {
        Function<List<String>, Process> dbusFails = command -> command.get(0).equals("gdbus") ? exited(1) : exited(0);

        launcher(Os.LINUX, Map.of(), dbusFails).reveal(FOLDER, true);

        assertEquals(List.of(
                dbus("ShowFolders", FOLDER.toUri().toString()),
                List.of("xdg-open", FOLDER.toString())), started);
    }

    @Test
    void linuxXdgOpenFailureIs503() {
        // xdg-open exit code 3: no tool found for the action
        FileManagerLauncher noTool = launcher(Os.LINUX, Map.of(), command -> exited(3));

        assertEquals(503, assertThrows(ProblemException.class, () -> noTool.reveal(FOLDER, true)).status());
        assertEquals(503, assertThrows(ProblemException.class, () -> noTool.reveal(FILE, false)).status());
    }

    @Test
    void linuxXdgOpenStillRunningCountsAsStarted() {
        FileManagerLauncher foreground = launcher(Os.LINUX, Map.of(), command -> running());

        assertDoesNotThrow(() -> foreground.reveal(FOLDER, true));
    }

    // --- all ---

    @Test
    void nothingStartedIs503() {
        for (Os os : Os.values()) {
            FileManagerLauncher launcher = new FileManagerLauncher(os, command -> {
                started.add(command);
                throw new IOException("not installed");
            }, WINDOWS_ENV, Duration.ofMillis(10), Duration.ofMillis(10));

            ProblemException problem = assertThrows(ProblemException.class, () -> launcher.reveal(FILE, false));

            assertEquals(503, problem.status());
            assertEquals(FileManagerLauncher.NOT_STARTED, problem.detail());
        }
    }

    private static List<String> dbusShowItems(String uri) {
        return dbus("ShowItems", uri);
    }

    private static List<String> dbus(String method, String uri) {
        return List.of("gdbus", "call", "--session",
                "--dest", "org.freedesktop.FileManager1",
                "--object-path", "/org/freedesktop/FileManager1",
                "--method", "org.freedesktop.FileManager1." + method,
                "['" + uri + "']", "");
    }

    private FileManagerLauncher launcher(Os os, Map<String, String> env, Function<List<String>, Process> processes) {
        return new FileManagerLauncher(os, command -> {
            started.add(command);
            return processes.apply(command);
        }, env, Duration.ofMillis(10), Duration.ofMillis(10));
    }

    private static Process exited(int exitCode) {
        return new FakeProcess(exitCode, true);
    }

    private static Process running() {
        return new FakeProcess(0, false);
    }

    private static final class FakeProcess extends Process {

        private final int exitCode;
        private final boolean finishes;
        private boolean destroyed;

        FakeProcess(int exitCode, boolean finishes) {
            this.exitCode = exitCode;
            this.finishes = finishes;
        }

        @Override
        public OutputStream getOutputStream() {
            return OutputStream.nullOutputStream();
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(new byte[0]);
        }

        @Override
        public InputStream getErrorStream() {
            return new ByteArrayInputStream(new byte[0]);
        }

        @Override
        public int waitFor() {
            return exitCode;
        }

        @Override
        public boolean waitFor(long timeout, TimeUnit unit) {
            return finishes;
        }

        @Override
        public int exitValue() {
            if (!finishes) {
                throw new IllegalThreadStateException("still running");
            }
            return exitCode;
        }

        @Override
        public void destroy() {
            destroyed = true;
        }
    }
}
