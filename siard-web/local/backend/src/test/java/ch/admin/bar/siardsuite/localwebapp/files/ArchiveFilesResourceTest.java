package ch.admin.bar.siardsuite.localwebapp.files;

import static ch.admin.bar.siardsuite.localwebapp.support.Sessions.authenticated;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ch.admin.bar.siardsuite.webcore.archive.ArchiveRegistry;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;

/**
 * Spec requirement "Open an archive from a local path" of local-web-archive-inspection. The
 * fixture is copied to a temporary folder, so a bug that changed the file could not damage it.
 */
@QuarkusTest
class ArchiveFilesResourceTest {

    @Inject
    ArchiveRegistry registry;

    @TempDir
    Path dir;

    private Path archive;

    @BeforeEach
    void copyFixture() throws IOException {
        archive = Files.copy(Path.of(System.getProperty("siard.testfiles"), "sample.siard"), dir.resolve("sample.siard"));
    }

    @AfterEach
    void closeAll() {
        registry.closeAll();
    }

    @Test
    void validArchiveIsOpenedInPlaceAndLeftUnchanged() throws IOException {
        byte[] before = sha256(archive);

        String archiveId = open(archive.toString())
                .then()
                .statusCode(201)
                .body("archiveId", notNullValue())
                .extract().path("archiveId");
        authenticated().get("/api/archives/" + archiveId)
                .then()
                .statusCode(200)
                .body("dbName", notNullValue());
        authenticated().delete("/api/archives/" + archiveId).then().statusCode(204);

        assertTrue(Files.isRegularFile(archive), "the file is still there");
        assertArrayEquals(before, sha256(archive), "the file is unchanged");
        assertEquals(List.of(archive), listFiles(dir), "no copy was left next to it");
    }

    @Test
    void unusablePathOrFile() throws IOException {
        Path notAnArchive = Files.writeString(dir.resolve("notes.siard"), "not a SIARD archive");

        problem(open("relative/sample.siard"), 400);
        problem(open(dir.resolve("missing.siard").toString()), 404);
        problem(open(dir.toString()), 400);
        problem(open(notAnArchive.toString()), 422);
        problem(authenticated().contentType("application/json").body("{}").post("/api/archive-files"), 400);
    }

    @Test
    void limitReachedIs429AndClosesNothing() {
        List<String> opened = new ArrayList<>();
        Response response = open(archive.toString());
        // the registry allows 5 per scope by default; open until the limit answers
        while (response.statusCode() == 201 && opened.size() < 10) {
            opened.add(response.path("archiveId"));
            response = open(archive.toString());
        }

        problem(response, 429);
        response.then().body("archiveId", nullValue());
        assertTrue(!opened.isEmpty() && opened.size() < 10, "opened " + opened.size());
        for (String archiveId : opened) {
            authenticated().get("/api/archives/" + archiveId).then().statusCode(200);
        }
    }

    @Test
    void openingNeedsASession() {
        given().contentType("application/json").body(Map.of("path", archive.toString())).post("/api/archive-files")
                .then()
                .statusCode(401)
                .body("archiveId", nullValue());

        assertEquals(0, registry.size(), "no archive was registered");
    }

    private static Response open(String path) {
        return authenticated().contentType("application/json").body(Map.of("path", path)).post("/api/archive-files");
    }

    private static void problem(Response response, int status) {
        response.then()
                .statusCode(status)
                .contentType("application/problem+json")
                .body("status", is(status))
                .body("detail", notNullValue());
    }

    private static byte[] sha256(Path file) throws IOException {
        try {
            return MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(file));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static List<Path> listFiles(Path folder) throws IOException {
        try (var files = Files.list(folder)) {
            return files.toList();
        }
    }
}
