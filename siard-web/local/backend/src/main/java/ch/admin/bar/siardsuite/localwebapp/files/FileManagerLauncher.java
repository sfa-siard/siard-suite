package ch.admin.bar.siardsuite.localwebapp.files;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.admin.bar.siardsuite.webcore.error.ProblemException;

/**
 * Opens the system file manager (design D5): a file is shown selected in its folder, a folder is
 * opened. Commands are started with separate arguments, never through a shell, and only for
 * paths the caller has checked to exist.
 */
@ApplicationScoped
public class FileManagerLauncher {

    private static final Logger LOG = LoggerFactory.getLogger(FileManagerLauncher.class);

    static final String NOT_STARTED = "The file browser could not be opened";
    static final Duration DBUS_WAIT = Duration.ofSeconds(5);
    static final Duration EXIT_WAIT = Duration.ofSeconds(2);

    /** Starts a process, replaceable in tests. */
    interface Starter {
        Process start(List<String> command) throws IOException;
    }

    private final Os os;
    private final Starter starter;
    private final Map<String, String> env;
    private final Duration dbusWait;
    private final Duration exitWait;

    FileManagerLauncher() {
        this(Os.current(), command -> new ProcessBuilder(command)
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .start(), System.getenv(), DBUS_WAIT, EXIT_WAIT);
    }

    FileManagerLauncher(Os os, Starter starter, Map<String, String> env, Duration dbusWait, Duration exitWait) {
        this.os = os;
        this.starter = starter;
        this.env = env;
        this.dbusWait = dbusWait;
        this.exitWait = exitWait;
    }

    /**
     * @param path   an existing absolute path
     * @param folder whether the path is a folder
     * @throws ProblemException 503 if no file manager could be started or the opener reported failure
     */
    public void reveal(Path path, boolean folder) {
        boolean started = switch (os) {
            // Explorer's exit code means nothing (often 1 on success); started is all we can know
            case WINDOWS -> start(folder
                    ? List.of(explorer(), path.toString())
                    : List.of(explorer(), "/select," + path));
            // -R also for folders: "open" runs a bundle folder such as Calculator.app instead of showing it
            case MAC -> startAndCheck(List.of("open", "-R", path.toString()));
            // the file manager over D-Bus first, like a file: xdg-open of a folder can report success
            // without showing a window (seen on GNOME)
            case LINUX -> folder
                    ? fileManager("ShowFolders", path) || startAndCheck(List.of("xdg-open", path.toString()))
                    : fileManager("ShowItems", path)
                            || startAndCheck(List.of("xdg-open", path.getParent().toString()));
        };
        if (!started) {
            throw new ProblemException(503, NOT_STARTED);
        }
    }

    // by its full path, never looked up through PATH or the working folder
    private String explorer() {
        String windowsFolder = firstSet("SystemRoot", "windir");
        return (windowsFolder == null ? "C:\\Windows" : windowsFolder) + "\\explorer.exe";
    }

    // Windows variable names are case-insensitive
    private String firstSet(String... names) {
        for (String name : names) {
            for (Map.Entry<String, String> variable : env.entrySet()) {
                if (variable.getKey().equalsIgnoreCase(name) && !variable.getValue().isBlank()) {
                    return variable.getValue();
                }
            }
        }
        return null;
    }

    // org.freedesktop.FileManager1: ShowItems selects a file in its folder, ShowFolders opens a folder,
    // in Nautilus, Dolphin, Nemo, Caja, ...
    private boolean fileManager(String method, Path path) {
        // the URI goes into a GVariant string literal; a quote would end it early
        String uri = path.toUri().toString().replace("'", "%27");
        List<String> command = List.of("gdbus", "call", "--session",
                "--dest", "org.freedesktop.FileManager1",
                "--object-path", "/org/freedesktop/FileManager1",
                "--method", "org.freedesktop.FileManager1." + method,
                "['" + uri + "']", "");
        try {
            Process process = starter.start(command);
            if (process.waitFor(dbusWait.toMillis(), TimeUnit.MILLISECONDS) && process.exitValue() == 0) {
                return true;
            }
            process.destroy();
            return false;
        } catch (IOException e) {
            LOG.debug("Could not start {}", command.get(0), e);
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    // open and xdg-open exit at once with their result; an opener still running after the wait
    // counts as started (xdg-open without a known desktop can stay in the foreground)
    private boolean startAndCheck(List<String> command) {
        try {
            Process process = starter.start(command);
            if (!process.waitFor(exitWait.toMillis(), TimeUnit.MILLISECONDS)) {
                return true;
            }
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                LOG.debug("{} exited with {}", command.get(0), exitCode);
            }
            return exitCode == 0;
        } catch (IOException e) {
            LOG.debug("Could not start {}", command.get(0), e);
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private boolean start(List<String> command) {
        try {
            starter.start(command);
            return true;
        } catch (IOException e) {
            LOG.debug("Could not start {}", command.get(0), e);
            return false;
        }
    }
}
