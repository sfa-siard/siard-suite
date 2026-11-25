package ch.admin.bar.siardsuite.localwebapp.files;

import static ch.admin.bar.siardsuite.localwebapp.support.Sessions.authenticated;
import static ch.admin.bar.siardsuite.localwebapp.support.Sessions.cookieName;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ch.admin.bar.siardsuite.webcore.error.ProblemException;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;

/**
 * Spec requirements "Places", "Folder listing", "Listing filters", "Large folders", "Entry lookup"
 * and "Show in file browser" of local-web-file-chooser, against temporary folders. Places and the
 * file manager are replaced per test, so no real file manager is ever started.
 */
@QuarkusTest
class FilesResourceTest {

    private static final boolean WINDOWS = System.getProperty("os.name", "").startsWith("Windows");

    @TempDir
    Path dir;

    // --- Folder listing ---

    @Test
    void foldersFirstThenFilesByNameIgnoringCase() throws IOException {
        Files.writeString(dir.resolve("b.siard"), "b");
        Files.writeString(dir.resolve("A.siard"), "four");
        Files.createDirectory(dir.resolve("data"));

        authenticated().queryParam("path", dir.toString()).get("/api/files")
                .then()
                .statusCode(200)
                .body("path", is(dir.toString()))
                .body("parent", is(dir.getParent().toString()))
                .body("entries.name", contains("data", "A.siard", "b.siard"))
                .body("entries[0].folder", is(true))
                .body("entries[0].size", nullValue())
                .body("entries[1].folder", is(false))
                .body("entries[1].size", is(4))
                .body("entries[1].path", is(dir.resolve("A.siard").toString()))
                .body("truncated", is(false));
    }

    @Test
    void rootHasNoParent() {
        authenticated().queryParam("path", dir.getRoot().toString()).get("/api/files")
                .then()
                .statusCode(200)
                .body("parent", nullValue());
    }

    @Test
    void relativePathIs400() {
        problem(authenticated().queryParam("path", "relative/folder").get("/api/files"), 400);
        problem(authenticated().get("/api/files"), 400);
    }

    @Test
    void missingFolderIs404() {
        problem(authenticated().queryParam("path", dir.resolve("missing").toString()).get("/api/files"), 404);
    }

    @Test
    void fileInsteadOfFolderIs400() throws IOException {
        Path file = Files.writeString(dir.resolve("a.siard"), "a");

        problem(authenticated().queryParam("path", file.toString()).get("/api/files"), 400);
    }

    @Test
    void unreadableFolderIs403() throws IOException {
        assumeFalse(WINDOWS, "POSIX permissions");
        Path locked = Files.createDirectory(dir.resolve("locked"));
        Files.setPosixFilePermissions(locked, PosixFilePermissions.fromString("---------"));
        try {
            assumeFalse(Files.isReadable(locked), "running as a user who can read everything");
            problem(authenticated().queryParam("path", locked.toString()).get("/api/files"), 403);
        } finally {
            Files.setPosixFilePermissions(locked, PosixFilePermissions.fromString("rwx------"));
        }
    }

    @Test
    void listingNeedsASession() throws IOException {
        Files.writeString(dir.resolve("a.siard"), "a");

        given().queryParam("path", dir.toString()).get("/api/files")
                .then()
                .statusCode(401)
                .body(not(containsString("a.siard")));
    }

    // --- Listing filters ---

    @Test
    void extensionFilterKeepsFolders() throws IOException {
        Files.writeString(dir.resolve("a.SIARD"), "a");
        Files.writeString(dir.resolve("notes.txt"), "n");
        Files.createDirectory(dir.resolve("old"));

        authenticated().queryParam("path", dir.toString()).queryParam("extension", ".siard").get("/api/files")
                .then()
                .statusCode(200)
                .body("entries.name", contains("old", "a.SIARD"));
    }

    @Test
    void hiddenEntriesAndFoldersOnly() throws IOException {
        Path hidden = Files.createDirectory(dir.resolve(".cache"));
        if (WINDOWS) {
            Files.setAttribute(hidden, "dos:hidden", true);
        }
        Files.createDirectory(dir.resolve("visible"));
        Files.writeString(dir.resolve("file.siard"), "f");

        authenticated().queryParam("path", dir.toString()).queryParam("foldersOnly", true).get("/api/files")
                .then()
                .statusCode(200)
                .body("entries.name", contains("visible"));
        authenticated().queryParam("path", dir.toString())
                .queryParam("foldersOnly", true).queryParam("hidden", true).get("/api/files")
                .then()
                .statusCode(200)
                .body("entries.name", contains(".cache", "visible"))
                .body("entries[0].hidden", is(true));
    }

    @Test
    void filteredListingNeedsAValidSession() {
        given().cookie(cookieName(), "invalid")
                .queryParam("path", dir.toString()).queryParam("extension", ".siard").get("/api/files")
                .then()
                .statusCode(401);
    }

    // --- Large folders ---

    private static final int SMALL_CAP = 3;

    @Test
    void folderAtTheCapIsComplete() throws IOException {
        installCap(SMALL_CAP);
        for (String name : List.of("a", "b", "c")) {
            Files.writeString(dir.resolve(name), name);
        }

        authenticated().queryParam("path", dir.toString()).get("/api/files")
                .then()
                .statusCode(200)
                .body("entries", hasSize(SMALL_CAP))
                .body("truncated", is(false));
    }

    @Test
    void oversizedFolderIsCut() throws IOException {
        installCap(SMALL_CAP);
        for (String name : List.of("d", "c", "b", "a")) {
            Files.createFile(dir.resolve(name));
        }

        authenticated().queryParam("path", dir.toString()).get("/api/files")
                .then()
                .statusCode(200)
                .body("entries.name", contains("a", "b", "c"))
                .body("truncated", is(true));
    }

    // the real lister with a small cap instead of the production 10000, so no test creates 10001 files
    private static void installCap(int maxEntries) {
        QuarkusMock.installMockForType(new FolderListings(new FolderLister(maxEntries)::list,
                FolderListings.WORKERS, FolderListings.TIME_LIMIT), FolderListings.class);
    }

    @Test
    void largeListingNeedsASession() {
        given().queryParam("path", dir.toString()).get("/api/files")
                .then()
                .statusCode(401)
                .body(not(containsString("entries")));
    }

    // --- Places ---

    @Test
    void placesOfTheCurrentUser() throws IOException {
        Path home = Files.createDirectories(dir.resolve("home"));
        Path documents = Files.createDirectory(home.resolve("Documents"));
        installPlaces(home);

        authenticated().get("/api/files/places")
                .then()
                .statusCode(200)
                .body("find { it.kind == 'home' }.path", is(home.toString()))
                .body("find { it.kind == 'documents' }.path", is(documents.toString()))
                .body("findAll { it.kind == 'root' }", not(hasSize(0)));
    }

    @Test
    void missingStandardFolderIsLeftOut() throws IOException {
        Path home = Files.createDirectories(dir.resolve("home"));
        installPlaces(home);

        authenticated().get("/api/files/places")
                .then()
                .statusCode(200)
                .body("findAll { it.kind == 'downloads' }", hasSize(0));
    }

    // Linux rules with a temporary home and file-system root, independent of the machine running the test
    private void installPlaces(Path home) {
        QuarkusMock.installMockForType(new Places(Os.LINUX, home, Map.of(), "me", dir, List::of,
                key -> Optional.empty(), Files::isDirectory, Duration.ofSeconds(2), Clock.systemUTC()), Places.class);
    }

    @Test
    void placesNeedASession() {
        given().get("/api/files/places")
                .then()
                .statusCode(401)
                .body(not(containsString(System.getProperty("user.home"))));
    }

    // --- Entry lookup ---

    @Test
    void existingFile() throws IOException {
        Path file = Files.writeString(dir.resolve("db.siard"), "db");

        authenticated().queryParam("path", file.toString()).get("/api/files/entry")
                .then()
                .statusCode(200)
                .body("path", is(file.toString()))
                .body("name", is("db.siard"))
                .body("parent", is(dir.toString()))
                .body("folder", is(false))
                .body("size", is(2))
                .body("writable", is(true));
    }

    @Test
    void missingOrMalformedEntryPath() {
        problem(authenticated().queryParam("path", dir.resolve("missing").toString()).get("/api/files/entry"), 404);
        problem(authenticated().queryParam("path", "relative.siard").get("/api/files/entry"), 400);
        problem(authenticated().queryParam("path", dir + "/a\u0000b").get("/api/files/entry"), 400);
    }

    @Test
    void lookupNeedsASession() throws IOException {
        Path file = Files.writeString(dir.resolve("db.siard"), "db");

        given().queryParam("path", file.toString()).get("/api/files/entry")
                .then()
                .statusCode(401)
                .body(not(containsString("db.siard")));
    }

    // --- Show in file browser ---

    @Test
    void revealExistingFileAndFolder() throws IOException {
        Path file = Files.writeString(dir.resolve("db.siard"), "db");
        RecordingLauncher launcher = new RecordingLauncher(false);
        QuarkusMock.installMockForType(launcher, FileManagerLauncher.class);

        reveal(file.toString()).then().statusCode(204);
        reveal(dir.toString()).then().statusCode(204);

        assertEquals(
                List.of(file + " file", dir + " folder"), launcher.revealed);
    }

    @Test
    void revealFailsSafely() {
        RecordingLauncher launcher = new RecordingLauncher(false);
        QuarkusMock.installMockForType(launcher, FileManagerLauncher.class);

        problem(reveal(dir.resolve("missing").toString()), 404);
        problem(reveal("relative.siard"), 400);
        problem(reveal(dir.resolve("x; rm -rf ~").toString()), 404);
        problem(authenticated().contentType("application/json").body("{}").post("/api/files/reveal"), 400);

        assertEquals(List.of(), launcher.revealed);
    }

    @Test
    void revealWithoutFileManagerIs503() throws IOException {
        Path file = Files.writeString(dir.resolve("db.siard"), "db");
        QuarkusMock.installMockForType(new RecordingLauncher(true), FileManagerLauncher.class);

        problem(reveal(file.toString()), 503);
    }

    @Test
    void revealNeedsASession() throws IOException {
        Path file = Files.writeString(dir.resolve("db.siard"), "db");
        RecordingLauncher launcher = new RecordingLauncher(false);
        QuarkusMock.installMockForType(launcher, FileManagerLauncher.class);

        given().contentType("application/json").body(Map.of("path", file.toString())).post("/api/files/reveal")
                .then()
                .statusCode(401);

        assertEquals(List.of(), launcher.revealed);
    }

    private static Response reveal(String path) {
        return authenticated().contentType("application/json").body(Map.of("path", path)).post("/api/files/reveal");
    }

    private static final class RecordingLauncher extends FileManagerLauncher {

        private final List<String> revealed = new CopyOnWriteArrayList<>();
        private final boolean fails;

        RecordingLauncher(boolean fails) {
            super(Os.LINUX, command -> {
                throw new IOException("never started in tests");
            }, Map.of(), Duration.ZERO, Duration.ZERO);
            this.fails = fails;
        }

        @Override
        public void reveal(Path path, boolean folder) {
            if (fails) {
                throw new ProblemException(503, NOT_STARTED);
            }
            revealed.add(path + (folder ? " folder" : " file"));
        }
    }

    // --- stalled locations (design Risks) ---

    @Test
    void stalledLocationAnswers504ThenFullPool503WithoutRetryAfter() {
        CountDownLatch release = new CountDownLatch(1);
        QuarkusMock.installMockForType(new FolderListings((folder, filter) -> {
            while (true) {
                try {
                    release.await();
                    return new FolderListing(folder.toString(), null, List.of(), false);
                } catch (InterruptedException e) {
                    // a blocked file-system call ignores interrupts
                }
            }
        }, 1, Duration.ofMillis(200)), FolderListings.class);
        try {
            authenticated().queryParam("path", dir.toString()).get("/api/files")
                    .then()
                    .statusCode(504)
                    .contentType("application/problem+json")
                    .header("Retry-After", nullValue());
            authenticated().queryParam("path", dir.toString()).get("/api/files")
                    .then()
                    .statusCode(503)
                    .contentType("application/problem+json")
                    .header("Retry-After", nullValue());
        } finally {
            release.countDown();
        }
    }

    private static void problem(Response response, int status) {
        response.then()
                .statusCode(status)
                .contentType("application/problem+json")
                .body("status", is(status))
                .body("detail", notNullValue());
    }
}
