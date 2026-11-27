package ch.admin.bar.siardsuite.localwebapp.files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.abort;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import ch.admin.bar.siardsuite.localwebapp.support.MutableClock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** The per-OS place rules of design D4, each run on any machine with a fake environment. */
class PlacesTest {

    private static final Duration LIMIT = Duration.ofMillis(200);
    private static final Path WINDOWS_HOME = Path.of("C:\\Users\\me");

    @TempDir
    Path dir;

    private final CountDownLatch release = new CountDownLatch(1);
    private final MutableClock clock = new MutableClock();

    @AfterEach
    void releaseChecks() {
        release.countDown();
    }

    // --- Windows ---

    @Test
    void windowsReadsShellFoldersIncludingOneDriveRedirection() {
        String export = reg(
                "\"Desktop\"=" + hex2("%USERPROFILE%\\Desktop"),
                "\"Personal\"=\"C:\\\\Users\\\\me\\\\OneDrive\\\\Documents\"",
                "\"{374DE290-123F-4565-9164-39C4925E467B}\"=" + hex2("%userprofile%\\Downloads"),
                "\"My Music\"=" + hex2("%USERPROFILE%\\Music"));
        Set<String> existing = Set.of("C:\\Users\\me\\Desktop", "C:\\Users\\me\\OneDrive\\Documents",
                "C:\\Users\\me\\Downloads");

        Places places = windows(key -> Optional.of(export), path -> existing.contains(path.toString()),
                List.of(Path.of("C:\\"), Path.of("D:\\")));

        assertEquals(List.of(
                new Place("home", "C:\\Users\\me"),
                new Place("desktop", "C:\\Users\\me\\Desktop"),
                new Place("documents", "C:\\Users\\me\\OneDrive\\Documents"),
                new Place("downloads", "C:\\Users\\me\\Downloads"),
                new Place("root", "C:\\"),
                new Place("root", "D:\\")), places.places());
    }

    @Test
    void windowsKeepsNonAsciiNamesFromTheExport() {
        String export = reg(
                "\"Desktop\"=" + hex2("%USERPROFILE%\\Bureau"),
                "\"Personal\"=\"C:\\\\Users\\\\Zoë\\\\Données\"");

        Places places = new Places(Os.WINDOWS, Path.of("C:\\Users\\Zoë"), Map.of("USERPROFILE", "C:\\Users\\Zoë"),
                "Zoë", dir, () -> List.of(Path.of("C:\\")), key -> Optional.of(export), path -> true, LIMIT, clock);

        assertEquals(List.of(
                new Place("home", "C:\\Users\\Zoë"),
                new Place("desktop", "C:\\Users\\Zoë\\Bureau"),
                new Place("documents", "C:\\Users\\Zoë\\Données"),
                new Place("downloads", Path.of("C:\\Users\\Zoë").resolve("Downloads").toString()),
                new Place("root", "C:\\")), places.places());
    }

    @Test
    void regFileIsDecodedAsUtf16IndependentOfTheConsoleCodePage() throws IOException {
        String text = reg("\"Personal\"=\"C:\\\\Users\\\\Zoë\\\\Données\"");
        Path file = dir.resolve("export.reg");
        byte[] content = text.getBytes(StandardCharsets.UTF_16LE);
        byte[] withBom = new byte[content.length + 2];
        withBom[0] = (byte) 0xFF;
        withBom[1] = (byte) 0xFE;
        System.arraycopy(content, 0, withBom, 2, content.length);
        Files.write(file, withBom);

        assertEquals(Map.of("Personal", "C:\\Users\\Zoë\\Données"), Places.regValues(Places.readRegFile(file)));
    }

    @Test
    void wrappedHexValuesAreJoined() {
        String value = "C:\\Users\\Zoë\\A folder with a rather long name that reg export wraps";
        String hex = hex2(value);
        String wrapped = hex.substring(0, 40) + "\\\r\n  " + hex.substring(40, 120) + "\\\r\n  " + hex.substring(120);

        assertEquals(Map.of("Desktop", value), Places.regValues(reg("\"Desktop\"=" + wrapped)));
    }

    @Test
    void networkFolderIsNotCheckedAndSlowCheckDoesNotStall() {
        String export = reg(
                "\"Personal\"=\"\\\\\\\\nas\\\\home\\\\me\\\\Documents\"",
                "\"Desktop\"=\"H:\\\\Desktop\"",
                "\"{374DE290-123F-4565-9164-39C4925E467B}\"=\"J:\\\\Downloads\"");
        List<Path> checked = new ArrayList<>();
        Predicate<Path> isFolder = path -> {
            synchronized (checked) {
                checked.add(path);
            }
            awaitUninterruptibly(release); // two unreachable mapped drives, checked in parallel
            return false;
        };

        long start = System.nanoTime();
        List<Place> result = windows(key -> Optional.of(export), isFolder, List.of(Path.of("C:\\"))).places();
        long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

        assertTrue(result.contains(new Place("documents", "\\\\nas\\home\\me\\Documents")));
        assertTrue(result.contains(new Place("desktop", "H:\\Desktop")), "kept when the check does not finish");
        assertTrue(result.contains(new Place("downloads", "J:\\Downloads")), "kept when the check does not finish");
        assertTrue(elapsed < LIMIT.toMillis() * 2, "one shared deadline, took " + elapsed + " ms");
        synchronized (checked) {
            assertTrue(checked.stream().noneMatch(path -> path.toString().startsWith("\\\\")), checked.toString());
        }
    }

    @Test
    void timedOutFolderIsNotRecheckedForAMinute() {
        String export = reg("\"Personal\"=\"H:\\\\Documents\"");
        AtomicInteger checks = new AtomicInteger();
        Places places = windows(key -> Optional.of(export), path -> {
            if (path.toString().startsWith("H:")) {
                checks.incrementAndGet();
                awaitUninterruptibly(release); // a dead mapped drive
            }
            return false;
        }, List.of());

        assertTrue(places.places().contains(new Place("documents", "H:\\Documents")));
        long start = System.nanoTime();
        assertTrue(places.places().contains(new Place("documents", "H:\\Documents")));
        assertTrue(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) < LIMIT.toMillis(),
                "remembered, not waited for");
        assertEquals(1, checks.get());

        clock.advance(Places.REMEMBER_UNRESPONSIVE.plusSeconds(1));
        places.places();
        assertEquals(2, checks.get());
    }

    @Test
    void atMostCheckWorkersChecksRunAndARejectedCheckCountsAsPresent() {
        // the scenario: the first call leaves one stuck check per standard folder, so the second call
        // finds exactly one free worker; that holds only while CHECK_WORKERS == standard folders + 1
        int standardFolders = 3; // Desktop, Documents, Downloads
        assertEquals(standardFolders + 1, Places.CHECK_WORKERS, "adapt this test to the new worker count");
        AtomicInteger started = new AtomicInteger();
        Places places = windows(key -> Optional.empty(), path -> {
            started.incrementAndGet();
            awaitUninterruptibly(release); // every folder on a dead drive
            return false;
        }, List.of());

        places.places(); // one stuck check per standard folder
        clock.advance(Places.REMEMBER_UNRESPONSIVE.plusSeconds(1));
        long start = System.nanoTime();
        List<Place> second = places.places(); // one check gets the last free worker, the others are rejected
        long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

        assertEquals(standardFolders + 1, started.get(), "never more checks than workers");
        assertEquals(1 + standardFolders, second.size(), "home and every standard folder, kept unchecked: " + second);
        assertTrue(elapsed < LIMIT.toMillis() * 2, "took " + elapsed + " ms");
    }

    @Test
    void successfulLookupIsCachedFailedOneRetried() {
        AtomicInteger exports = new AtomicInteger();
        Places working = windows(key -> {
            exports.incrementAndGet();
            return Optional.of(reg("\"Desktop\"=" + hex2("C:\\Users\\me\\Desktop")));
        }, path -> true, List.of());
        working.places();
        working.places();
        assertEquals(1, exports.get());

        AtomicInteger failures = new AtomicInteger();
        Places failing = windows(key -> {
            failures.incrementAndGet();
            return Optional.empty();
        }, path -> true, List.of());
        failing.places();
        failing.places();
        assertEquals(2, failures.get());
    }

    @Test
    void duplicatePlacesAreRemovedIgnoringCaseOnWindows() {
        String export = reg(
                "\"Desktop\"=\"C:\\\\Users\\\\me\\\\OneDrive\"",
                "\"Personal\"=\"c:\\\\users\\\\me\\\\onedrive\"",
                "\"{374DE290-123F-4565-9164-39C4925E467B}\"=\"C:\\\\Users\\\\me\"");

        Places places = windows(key -> Optional.of(export), path -> true,
                List.of(Path.of("C:\\"), Path.of("c:\\")));

        assertEquals(List.of(
                new Place("home", "C:\\Users\\me"),
                new Place("desktop", "C:\\Users\\me\\OneDrive"),
                new Place("root", "C:\\")), places.places());
    }

    // --- macOS ---

    @Test
    void macListsHomeFoldersRootAndVolumesWithoutTheBootLink() throws IOException {
        Path home = Files.createDirectories(dir.resolve("Users/me"));
        Files.createDirectory(home.resolve("Desktop"));
        Files.createDirectory(home.resolve("Downloads"));
        Path volumes = Files.createDirectory(dir.resolve("Volumes"));
        Files.createDirectory(volumes.resolve("Backup"));
        Files.createDirectory(volumes.resolve("Share"));
        try {
            Files.createSymbolicLink(volumes.resolve("Macintosh HD"), dir);
        } catch (IOException | UnsupportedOperationException e) {
            // Windows without admin rights or Developer Mode may not create links
            abort("symbolic links cannot be created here: " + e);
        }

        Places places = new Places(Os.MAC, home, Map.of(), "me", dir, List::of, key -> Optional.empty(),
                Files::isDirectory, LIMIT, clock);

        assertEquals(List.of(
                new Place("home", home.toString()),
                new Place("desktop", home.resolve("Desktop").toString()),
                new Place("downloads", home.resolve("Downloads").toString()),
                new Place("root", dir.toString()),
                new Place("root", volumes.resolve("Backup").toString()),
                new Place("root", volumes.resolve("Share").toString())), places.places());
    }

    // --- Linux ---

    @Test
    void linuxReadsUserDirsAndMountFolders() throws IOException {
        Path home = Files.createDirectories(dir.resolve("home/me"));
        Files.createDirectories(home.resolve("Schreibtisch"));
        Files.createDirectories(home.resolve("Dokumente"));
        Files.createDirectories(home.resolve(".config"));
        Files.writeString(home.resolve(".config/user-dirs.dirs"), """
                # written by xdg-user-dirs-update
                XDG_DESKTOP_DIR="$HOME/Schreibtisch"
                XDG_DOCUMENTS_DIR="$HOME/Dokumente"
                XDG_DOWNLOAD_DIR="$HOME/"
                XDG_MUSIC_DIR="$HOME/Musik"
                """);
        Path stick = Files.createDirectories(dir.resolve("media/me/STICK"));
        Path usb = Files.createDirectories(dir.resolve("run/media/me/USB"));
        Path nas = Files.createDirectories(dir.resolve("mnt/nas"));

        Places places = linux(home, Map.of());

        assertEquals(List.of(
                new Place("home", home.toString()),
                new Place("desktop", home.resolve("Schreibtisch").toString()),
                new Place("documents", home.resolve("Dokumente").toString()),
                new Place("root", dir.toString()),
                new Place("root", stick.toString()),
                new Place("root", usb.toString()),
                new Place("root", nas.toString())), places.places());
    }

    @Test
    void linuxWithoutUserDirsFallsBackAndHonoursXdgConfigHome() throws IOException {
        Path home = Files.createDirectories(dir.resolve("home/me"));
        Files.createDirectories(home.resolve("Documents"));
        Path config = Files.createDirectories(dir.resolve("config"));
        Files.createDirectories(home.resolve("Arbeit"));
        Files.writeString(config.resolve("user-dirs.dirs"), "XDG_DESKTOP_DIR=\"$HOME/Arbeit\"\n");

        assertEquals(List.of(
                new Place("home", home.toString()),
                new Place("desktop", home.resolve("Arbeit").toString()),
                new Place("documents", home.resolve("Documents").toString()),
                new Place("root", dir.toString())), linux(home, Map.of("XDG_CONFIG_HOME", config.toString())).places());
        assertEquals(List.of(
                new Place("home", home.toString()),
                new Place("documents", home.resolve("Documents").toString()),
                new Place("root", dir.toString())), linux(home, Map.of()).places());
    }

    @Test
    void linuxDesktopEqualToDocumentsIsListedOnce() throws IOException {
        Path home = Files.createDirectories(dir.resolve("home/me"));
        Files.createDirectories(home.resolve("Documents"));
        Files.createDirectories(home.resolve(".config"));
        Files.writeString(home.resolve(".config/user-dirs.dirs"), "XDG_DESKTOP_DIR=\"$HOME/Documents\"\n");

        assertEquals(List.of(
                new Place("home", home.toString()),
                new Place("desktop", home.resolve("Documents").toString()),
                new Place("root", dir.toString())), linux(home, Map.of()).places());
    }

    private Places windows(Places.RegistryExport registry, Predicate<Path> isFolder, List<Path> roots) {
        return new Places(Os.WINDOWS, WINDOWS_HOME, Map.of("USERPROFILE", "C:\\Users\\me"), "me", dir,
                () -> roots, registry, isFolder, LIMIT, clock);
    }

    private Places linux(Path home, Map<String, String> env) {
        return new Places(Os.LINUX, home, env, "me", dir, List::of, key -> Optional.empty(), Files::isDirectory, LIMIT, clock);
    }

    /** A .reg export as reg writes it (before UTF-16 encoding). */
    private static String reg(String... values) {
        return "Windows Registry Editor Version 5.00\r\n\r\n"
                + "[HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\User Shell Folders]\r\n"
                + String.join("\r\n", values) + "\r\n";
    }

    /** A REG_EXPAND_SZ value as reg export writes it: UTF-16LE bytes with terminator. */
    private static String hex2(String value) {
        byte[] bytes = (value + "\0").getBytes(StandardCharsets.UTF_16LE);
        List<String> parts = new ArrayList<>();
        for (byte b : bytes) {
            parts.add(HexFormat.of().toHexDigits(b));
        }
        return "hex(2):" + String.join(",", parts);
    }

    private static void awaitUninterruptibly(CountDownLatch latch) {
        while (true) {
            try {
                latch.await();
                return;
            } catch (InterruptedException e) {
                // a blocked file-system call ignores interrupts
            }
        }
    }
}
