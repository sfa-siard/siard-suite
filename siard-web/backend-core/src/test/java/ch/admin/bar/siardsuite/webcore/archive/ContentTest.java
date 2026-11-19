package ch.admin.bar.siardsuite.webcore.archive;

import ch.admin.bar.siardsuite.webcore.error.ProblemDetail;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.fixture;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.get;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.newScope;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spec requirement "Large object content" (the aborted download is in {@link AbortedDownloadTest}).
 * Column 9 of TSIMPLE ({@code CBLOB}) holds the LOB files {@code lob9/record<row>.bin}.
 */
@QuarkusTest
class ContentTest {

    static final int CBLOB = 9;

    @Inject
    ArchiveRegistry registry;

    @TempDir
    Path tmp;

    @AfterEach
    void closeAll() {
        registry.closeAll();
    }

    private ArchiveScope scope;

    private String open(Path file) throws IOException {
        scope = newScope();
        return "/api/archives/" + registry.register(scope, file, () -> { }).value() + "/schemas/0/tables/0";
    }

    @Test
    void downloadOfABinaryLob() throws IOException {
        String table = open(fixture("sample.siard"));
        long length = get(scope, table + "/rows?offset=1&limit=1").then().statusCode(200)
                .extract().jsonPath().getLong("rows[0].cells[" + CBLOB + "].length");

        Response content = get(scope, table + "/rows/1/columns/" + CBLOB + "/content");

        content.then().statusCode(200);
        byte[] body = content.asByteArray();
        assertEquals(length, body.length);
        assertArrayEquals(CraftedArchive.entry("sample.siard", "content/schema0/table0/lob9/record1.bin"), body);
    }

    @Test
    void cellWithoutContent() throws IOException {
        String table = open(fixture("sample.siard"));
        JsonPath rows = get(scope, table + "/rows").then().statusCode(200).extract().jsonPath();
        List<String> names = get(scope, table).then().extract().jsonPath().getList("columns.name");
        int integer = names.indexOf("CINTEGER");
        int[] nullCell = firstNullCell(rows);

        get(scope, table + "/rows/" + nullCell[0] + "/columns/" + nullCell[1] + "/content").then()
                .statusCode(404).contentType(startsWith(ProblemDetail.MEDIA_TYPE));
        get(scope, table + "/rows/4/columns/0/content").then()
                .statusCode(404).contentType(startsWith(ProblemDetail.MEDIA_TYPE));
        String number = rows.getString("rows[0].cells[" + integer + "].value");
        byte[] body = get(scope, table + "/rows/0/columns/" + integer + "/content").then()
                .statusCode(200).extract().asByteArray();
        assertEquals(number, new String(body, StandardCharsets.UTF_8));
    }

    private static int[] firstNullCell(JsonPath rows) {
        List<List<Object>> cells = rows.getList("rows.cells");
        for (int r = 0; r < cells.size(); r++) {
            int c = cells.get(r).indexOf(null);
            if (c >= 0) {
                return new int[]{r, c};
            }
        }
        throw new AssertionError("TSIMPLE has no NULL cell");
    }

    @Test
    void activeContentIsNeverRenderedByTheBrowser() throws IOException {
        /**
         * Even if an archive contains a script dressed up as a web page, the browser only ever offers it as a harmless download with a neutral name. It’s never shown, sniffed or run inside the app.
         */
        Path crafted = CraftedArchive.of("sample.siard")
                .replaceLob("content/schema0/table0/lob9/record0.bin", "content/schema0/table0/lob9/record0.html",
                        "<script>alert(1)</script>".getBytes(StandardCharsets.UTF_8))
                .writeTo(tmp);
        String table = open(crafted);

        Response content = get(scope, table + "/rows/0/columns/" + CBLOB + "/content");

        content.then().statusCode(200).contentType(startsWith("application/octet-stream"));
        String disposition = content.header("Content-Disposition");
        assertTrue(disposition.startsWith("attachment"), disposition);
        assertEquals("nosniff", content.header("X-Content-Type-Options"));
        String fileName = disposition.replaceFirst(".*filename=\"([^\"]*)\".*", "$1");
        assertTrue(fileName.matches("[A-Za-z0-9._-]+"), fileName);
    }
}
