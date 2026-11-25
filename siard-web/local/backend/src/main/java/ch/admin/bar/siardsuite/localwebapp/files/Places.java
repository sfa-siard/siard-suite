package ch.admin.bar.siardsuite.localwebapp.files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The file chooser's starting places per OS (design D4): home, the Desktop, Documents and
 * Downloads folders that exist, and the file-system roots, each path once. Roots are never listed
 * or probed beyond reading their parent folder, Windows network paths are never touched, and every
 * other existence check has a time limit and a cap on concurrent checks, so a dead share cannot stall this.
 */
@ApplicationScoped
public class Places {

    private static final Logger LOG = LoggerFactory.getLogger(Places.class);

    static final String SHELL_FOLDERS_KEY =
            "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\User Shell Folders";
    static final Duration COMMAND_LIMIT = Duration.ofSeconds(5);
    static final Duration CHECK_LIMIT = Duration.ofSeconds(2);
    static final int CHECK_WORKERS = 4;
    static final Duration REMEMBER_UNRESPONSIVE = Duration.ofMinutes(1);

    // registry value names of the shell folders; Downloads only has its known-folder id
    private static final Map<String, String> WINDOWS_VALUES = Map.of(
            "Desktop", Place.DESKTOP,
            "Personal", Place.DOCUMENTS,
            "{374DE290-123F-4565-9164-39C4925E467B}", Place.DOWNLOADS);
    private static final Map<String, String> XDG_VARIABLES = Map.of(
            "XDG_DESKTOP_DIR", Place.DESKTOP,
            "XDG_DOCUMENTS_DIR", Place.DOCUMENTS,
            "XDG_DOWNLOAD_DIR", Place.DOWNLOADS);
    private static final Map<String, String> DEFAULT_FOLDERS = Map.of(
            Place.DESKTOP, "Desktop",
            Place.DOCUMENTS, "Documents",
            Place.DOWNLOADS, "Downloads");
    private static final List<String> STANDARD_KINDS = List.of(Place.DESKTOP, Place.DOCUMENTS, Place.DOWNLOADS);

    private static final Pattern REG_VALUE = Pattern.compile("^\"((?:[^\"\\\\]|\\\\.)*)\"=(.*)$");
    private static final Pattern REG_ESCAPE = Pattern.compile("\\\\(.)");
    private static final Pattern XDG_LINE = Pattern.compile("^\\s*(XDG_\\w+_DIR)=\"(.*)\"\\s*$");
    private static final Pattern WINDOWS_VARIABLE = Pattern.compile("%([^%]+)%");

    /** Exports a registry key as the text of a {@code .reg} file, empty when that fails; replaceable in tests. */
    interface RegistryExport {
        Optional<String> export(String key);
    }

    private final Os os;
    private final Path home;
    private final Map<String, String> env;
    private final String user;
    private final Path fileSystemRoot;
    private final Supplier<List<Path>> windowsRoots;
    private final RegistryExport registry;
    private final Predicate<Path> isFolder;
    private final Duration checkLimit;
    private final Clock clock;
    // at most CHECK_WORKERS checks run at once; a permit is released only when its check returns,
    // so stuck checks on a dead drive cannot pile up threads
    private final Semaphore checkPermits = new Semaphore(CHECK_WORKERS);
    // folders whose check timed out, with the time until which they are not checked again
    private final Map<Path, Instant> unresponsive = new ConcurrentHashMap<>();
    private final ExecutorService checks = Executors.newCachedThreadPool(task -> {
        Thread thread = new Thread(task, "place-check");
        thread.setDaemon(true);
        return thread;
    });

    private volatile Map<String, Path> shellFolders;

    Places() {
        this(Os.current(), Path.of(System.getProperty("user.home")), System.getenv(),
                System.getProperty("user.name"), Path.of("/"),
                () -> Arrays.stream(File.listRoots()).map(File::toPath).toList(),
                Places::exportRegistryKey, Files::isDirectory, CHECK_LIMIT, Clock.systemUTC());
    }

    Places(Os os, Path home, Map<String, String> env, String user, Path fileSystemRoot,
            Supplier<List<Path>> windowsRoots, RegistryExport registry, Predicate<Path> isFolder,
            Duration checkLimit, Clock clock) {
        this.os = os;
        this.home = home;
        this.env = env;
        this.user = user;
        this.fileSystemRoot = fileSystemRoot;
        this.windowsRoots = windowsRoots;
        this.registry = registry;
        this.isFolder = isFolder;
        this.checkLimit = checkLimit;
        this.clock = clock;
    }

    public List<Place> places() {
        Map<String, Place> places = new LinkedHashMap<>();
        add(places, Place.HOME, home);
        Map<String, Path> standard = standardFolders();
        Map<String, Path> candidates = new LinkedHashMap<>();
        for (String kind : STANDARD_KINDS) {
            candidates.put(kind, standard.getOrDefault(kind, home.resolve(DEFAULT_FOLDERS.get(kind))));
        }
        Map<String, Boolean> exists = existWithinLimit(candidates);
        candidates.forEach((kind, folder) -> {
            if (exists.get(kind)) {
                add(places, kind, folder);
            }
        });
        roots().forEach(root -> add(places, Place.ROOT, root));
        return List.copyOf(places.values());
    }

    // the first kind wins for a path listed twice (e.g. Desktop redirected to the home folder)
    private void add(Map<String, Place> places, String kind, Path path) {
        String key = path.normalize().toString();
        places.putIfAbsent(os == Os.WINDOWS ? key.toLowerCase(Locale.ROOT) : key, new Place(kind, path.toString()));
    }

    // network folders stay unchecked; the others are checked in parallel with one shared deadline.
    // A folder is kept unchecked when its check timed out within the last minute or when all check
    // workers are busy; a check that does not finish keeps it too. The chooser's listing then
    // reports the location as not responding
    private Map<String, Boolean> existWithinLimit(Map<String, Path> folders) {
        Map<String, Future<Boolean>> pending = new LinkedHashMap<>();
        Map<String, Boolean> exists = new LinkedHashMap<>();
        folders.forEach((kind, folder) -> {
            if (os == Os.WINDOWS && folder.toString().startsWith("\\\\")) {
                exists.put(kind, true);
            } else if (recentlyUnresponsive(folder)) {
                exists.put(kind, true);
            } else if (!checkPermits.tryAcquire()) {
                LOG.debug("All place checks are busy, keeping {} unchecked", folder);
                exists.put(kind, true);
            } else {
                pending.put(kind, submitCheck(folder));
            }
        });
        long deadline = System.nanoTime() + checkLimit.toNanos();
        pending.forEach((kind, check) -> {
            try {
                exists.put(kind, check.get(Math.max(0, deadline - System.nanoTime()), TimeUnit.NANOSECONDS));
            } catch (TimeoutException e) {
                LOG.debug("No answer from {} within {}", folders.get(kind), checkLimit);
                unresponsive.put(folders.get(kind), clock.instant().plus(REMEMBER_UNRESPONSIVE));
                exists.put(kind, true);
            } catch (ExecutionException e) {
                exists.put(kind, false);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                exists.put(kind, false);
            }
        });
        return exists;
    }

    private Future<Boolean> submitCheck(Path folder) {
        try {
            return checks.submit(() -> {
                try {
                    return isFolder.test(folder);
                } finally {
                    checkPermits.release();
                }
            });
        } catch (RuntimeException e) {
            checkPermits.release();
            throw e;
        }
    }

    private boolean recentlyUnresponsive(Path folder) {
        Instant until = unresponsive.get(folder);
        if (until == null) {
            return false;
        }
        if (clock.instant().isBefore(until)) {
            return true;
        }
        unresponsive.remove(folder, until);
        return false;
    }

    private Map<String, Path> standardFolders() {
        return switch (os) {
            case WINDOWS -> windowsShellFolders();
            case LINUX -> xdgUserDirs();
            case MAC -> Map.of();
        };
    }

    // "User Shell Folders" holds the current locations, also when OneDrive or a policy redirects
    // them; a successful lookup is kept, a failed one is retried next time
    private Map<String, Path> windowsShellFolders() {
        Map<String, Path> cached = shellFolders;
        if (cached != null) {
            return cached;
        }
        Optional<String> export = registry.export(SHELL_FOLDERS_KEY);
        if (export.isEmpty()) {
            return Map.of();
        }
        Map<String, Path> folders = new LinkedHashMap<>();
        regValues(export.get()).forEach((name, value) -> {
            if (WINDOWS_VALUES.containsKey(name)) {
                toPath(expandWindowsVariables(value)).ifPresent(path -> folders.put(WINDOWS_VALUES.get(name), path));
            }
        });
        shellFolders = Map.copyOf(folders);
        return shellFolders;
    }

    /** The string values of a {@code .reg} export: {@code "name"="text"} and {@code "name"=hex(2):..}. */
    static Map<String, String> regValues(String regText) {
        Map<String, String> values = new LinkedHashMap<>();
        for (String line : joinContinuations(regText)) {
            Matcher matcher = REG_VALUE.matcher(line);
            if (!matcher.matches()) {
                continue;
            }
            String data = matcher.group(2).trim();
            if (data.length() >= 2 && data.startsWith("\"") && data.endsWith("\"")) {
                values.put(unescape(matcher.group(1)), unescape(data.substring(1, data.length() - 1)));
            } else if (data.startsWith("hex(2):")) {
                values.put(unescape(matcher.group(1)), utf16Le(data.substring("hex(2):".length())));
            }
        }
        return values;
    }

    // long hex values are wrapped: a line ending in "\" continues on the next, indented line
    private static List<String> joinContinuations(String regText) {
        List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String line : regText.split("\\R")) {
            String part = current.isEmpty() ? line : line.trim();
            if (part.endsWith("\\")) {
                current.append(part, 0, part.length() - 1);
            } else {
                lines.add(current.append(part).toString());
                current.setLength(0);
            }
        }
        if (!current.isEmpty()) {
            lines.add(current.toString());
        }
        return lines;
    }

    private static String unescape(String text) {
        return REG_ESCAPE.matcher(text).replaceAll(match -> Matcher.quoteReplacement(match.group(1)));
    }

    private static String utf16Le(String hexBytes) {
        String[] parts = hexBytes.split(",");
        byte[] bytes = new byte[parts.length];
        int length = 0;
        for (String part : parts) {
            String hex = part.trim();
            if (!hex.isEmpty()) {
                bytes[length++] = (byte) Integer.parseInt(hex, 16);
            }
        }
        String text = new String(bytes, 0, length, StandardCharsets.UTF_16LE);
        int end = text.length();
        while (end > 0 && text.charAt(end - 1) == '\0') {
            end--;
        }
        return text.substring(0, end);
    }

    private String expandWindowsVariables(String value) {
        Map<String, String> variables = new LinkedHashMap<>();
        env.forEach((name, content) -> variables.put(name.toUpperCase(Locale.ROOT), content));
        Matcher matcher = WINDOWS_VARIABLE.matcher(value);
        StringBuilder expanded = new StringBuilder();
        while (matcher.find()) {
            String content = variables.get(matcher.group(1).toUpperCase(Locale.ROOT));
            matcher.appendReplacement(expanded, Matcher.quoteReplacement(content == null ? matcher.group() : content));
        }
        matcher.appendTail(expanded);
        return expanded.toString();
    }

    // ~/.config/user-dirs.dirs, written by xdg-user-dirs; values are "$HOME/..." or absolute
    private Map<String, Path> xdgUserDirs() {
        String configHome = env.get("XDG_CONFIG_HOME");
        Path config = configHome == null || configHome.isBlank() ? home.resolve(".config") : Path.of(configHome);
        Path file = config.resolve("user-dirs.dirs");
        if (!Files.isRegularFile(file)) {
            return Map.of();
        }
        Map<String, Path> folders = new LinkedHashMap<>();
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                Matcher matcher = XDG_LINE.matcher(line);
                if (matcher.matches() && XDG_VARIABLES.containsKey(matcher.group(1))) {
                    String value = matcher.group(2).replace("$HOME", home.toString());
                    toPath(value)
                            .filter(Path::isAbsolute)
                            // a folder set to $HOME itself is disabled (xdg-user-dirs convention)
                            .filter(path -> !path.normalize().equals(home.normalize()))
                            .ifPresent(path -> folders.put(XDG_VARIABLES.get(matcher.group(1)), path));
                }
            }
        } catch (IOException e) {
            LOG.debug("Could not read {}", file, e);
        }
        return folders;
    }

    private List<Path> roots() {
        return switch (os) {
            case WINDOWS -> windowsRoots.get();
            case MAC -> {
                List<Path> roots = new ArrayList<>(List.of(fileSystemRoot));
                // the boot volume appears in /Volumes as a link to /
                roots.addAll(children(fileSystemRoot.resolve("Volumes"), child -> !Files.isSymbolicLink(child)));
                yield roots;
            }
            case LINUX -> {
                List<Path> roots = new ArrayList<>(List.of(fileSystemRoot));
                if (user != null && !user.isBlank()) {
                    roots.addAll(children(fileSystemRoot.resolve("media").resolve(user), child -> true));
                    roots.addAll(children(fileSystemRoot.resolve("run/media").resolve(user), child -> true));
                }
                roots.addAll(children(fileSystemRoot.resolve("mnt"), child -> true));
                yield roots;
            }
        };
    }

    // reads only the parent folder, never the mounted locations themselves
    private static List<Path> children(Path folder, Predicate<Path> keep) {
        if (!Files.isDirectory(folder)) {
            return List.of();
        }
        List<Path> children = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
            for (Path child : stream) {
                if (keep.test(child)) {
                    children.add(child);
                }
            }
        } catch (IOException e) {
            LOG.debug("Could not list {}", folder, e);
        }
        children.sort(null);
        return children;
    }

    private static Optional<Path> toPath(String value) {
        try {
            return value.isBlank() ? Optional.empty() : Optional.of(Path.of(value));
        } catch (InvalidPathException e) {
            return Optional.empty();
        }
    }

    // reg export writes UTF-16 with BOM to the file, independent of the console code page; the
    // process writes no output to a pipe, so waitFor's limit holds
    private static Optional<String> exportRegistryKey(String key) {
        Path file = null;
        try {
            file = Files.createTempFile("siard-shell-folders", ".reg");
            Process process = new ProcessBuilder("reg", "export", key, file.toString(), "/y")
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .start();
            if (!process.waitFor(COMMAND_LIMIT.toMillis(), TimeUnit.MILLISECONDS)) {
                process.destroyForcibly();
                LOG.debug("reg export did not finish within {}", COMMAND_LIMIT);
                return Optional.empty();
            }
            return process.exitValue() == 0 ? Optional.of(readRegFile(file)) : Optional.empty();
        } catch (IOException e) {
            LOG.debug("Could not export {}", key, e);
            return Optional.empty();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } finally {
            if (file != null) {
                try {
                    Files.deleteIfExists(file);
                } catch (IOException e) {
                    LOG.debug("Could not delete {}", file, e);
                }
            }
        }
    }

    /** Decodes a {@code .reg} file: UTF-16 by its BOM (little-endian without one). */
    static String readRegFile(Path file) throws IOException {
        byte[] bytes = Files.readAllBytes(file);
        Charset charset = StandardCharsets.UTF_16LE;
        int offset = 0;
        if (bytes.length >= 2 && (bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xFE) {
            offset = 2;
        } else if (bytes.length >= 2 && (bytes[0] & 0xFF) == 0xFE && (bytes[1] & 0xFF) == 0xFF) {
            charset = StandardCharsets.UTF_16BE;
            offset = 2;
        }
        return new String(bytes, offset, bytes.length - offset, charset);
    }

    @PreDestroy
    void shutdown() {
        checks.shutdownNow();
    }
}
