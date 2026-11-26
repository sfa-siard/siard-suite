package ch.admin.bar.siardsuite.webcore.archive;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.Cell;
import ch.admin.bar.siard2.api.Table;
import ch.admin.bar.siard2.api.TableRecordDispenser;
import ch.admin.bar.siard2.api.facade.TableRecordFacade;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siardsuite.webcore.error.ProblemDetail;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.fixture;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.startsWith;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.get;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.newScope;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spec requirement "Cell values", on {@code sample.siard} and a crafted variant of it.
 */
@QuarkusTest
class CellValuesTest {

    /** The crafted text: {@code alpha42} at the start, {@code omega42} only at position 50000. */
    static final String LARGE_TEXT = "alpha42" + "x".repeat(50000 - 7) + "omega42" + "x".repeat(50000 - 7);
    /** An empty cell, as the desktop app shows a cell it cannot read. */
    static final Map<String, Object> EMPTY = Map.of("type", "text", "value", "", "truncated", false);
    /** {@code TSIMPLE} with a DATALINK column ({@code COLUMN_DATALINK}) whose files are 79 bytes of ASCII. */
    static final String DATALINK_FIXTURE = "sample-datalink-2-2.siard";
    static final String DATALINK_FILE = "content/schema0/table0/lob25/record0.bin";
    static final String TSIMPLE_SCHEMA = "content/schema0/table0/table0.xsd";
    static final String TSIMPLE_CLOB_FILE = "content/schema0/table0/lob2/record0.txt";

    @Inject
    ArchiveRegistry registry;

    @TempDir
    Path tmp;

    @AfterEach
    void closeAll() {
        registry.closeAll();
    }

    /** {@code sample.siard} with the CLOB of TSIMPLE row 0, column 2 replaced by {@link #LARGE_TEXT}. */
    static Path largeTextArchive(Path dir) throws IOException {
        return CraftedArchive.of("sample.siard")
                .replaceLob(TSIMPLE_CLOB_FILE, TSIMPLE_CLOB_FILE, LARGE_TEXT.getBytes(StandardCharsets.UTF_8))
                .writeTo(dir);
    }

    @Test
    void textNullAndNumericCells() throws IOException {
        ArchiveScope scope = newScope();
        String id = registry.register(scope, fixture("sample.siard"), () -> { }).value();
        JsonPath rows = get(scope, "/api/archives/" + id + "/schemas/0/tables/0/rows").then()
                .statusCode(200).extract().jsonPath();

        Archive archive = ArchiveImpl.newInstance();
        archive.openReadOnly(fixture("sample.siard").toFile());
        try {
            Table table = archive.getSchema(0).getTable(0);
            TableRecordDispenser records = table.openTableRecords();
            for (int r = 0; r < table.getMetaTable().getRows(); r++) {
                List<Cell> cells = new TableRecordFacade(records.get()).getCells();
                for (int c = 0; c < cells.size(); c++) {
                    Cell cell = cells.get(c);
                    Map<String, Object> rendered = rows.getMap("rows[" + r + "].cells[" + c + "]");
                    String where = "row " + r + ", column " + c;
                    if (cell.isNull()) {
                        assertEquals(Map.of("type", "null"), rendered, where);
                    } else if (!CellRenderer.isBinary(cell)) {
                        // what the desktop app shows (RowsOverviewForm.extractText), design D6
                        String shown = cell.getString();
                        assertEquals("text", rendered.get("type"), where);
                        assertEquals(shown.substring(0, Math.min(shown.length(), 4096)), rendered.get("value"), where);
                        assertEquals(shown.length() > 4096, rendered.get("truncated"), where);
                    }
                }
            }
            records.close();
        } finally {
            archive.close();
        }
    }

    @Test
    void largeTextIsTruncated() throws IOException {
        ArchiveScope scope = newScope();
        String archive = "/api/archives/" + registry.register(scope, largeTextArchive(tmp), () -> { }).value();

        Map<String, Object> cell = get(scope, archive + "/schemas/0/tables/0/rows?offset=0&limit=1").then()
                .statusCode(200).extract().jsonPath().getMap("rows[0].cells[2]");
        assertEquals(4096, ((String) cell.get("value")).length());
        assertEquals(true, cell.get("truncated"));
        byte[] content = get(scope, archive + "/schemas/0/tables/0/rows/0/columns/2/content").then()
                .statusCode(200).extract().asByteArray();
        assertArrayEquals(LARGE_TEXT.getBytes(StandardCharsets.UTF_8), content);
    }

    @Test
    void largeObjectCellsCarryNoContent() throws IOException {
        ArchiveScope scope = newScope();
        String archive = "/api/archives/" + registry.register(scope, fixture("sample.siard"), () -> { }).value();

        Response tsimple = get(scope, archive + "/schemas/0/tables/0/rows");
        tsimple.then().statusCode(200);
        long lobs = 0;
        long lobBytes = 0;
        for (Object cell : tsimple.jsonPath().getList("rows.cells.flatten()")) {
            if (cell instanceof Map<?, ?> map && "lob".equals(map.get("type"))) {
                long length = ((Number) map.get("length")).longValue();
                assertTrue(length > 0, map.toString());
                lobs++;
                lobBytes += length;
            }
        }
        assertTrue(lobs > 0, "TSIMPLE has binary cells");
        assertTrue(lobBytes > 1_000_000, "binary LOBs of TSIMPLE: " + lobBytes);
        assertTrue(tsimple.asByteArray().length < lobBytes, "TSIMPLE body: " + tsimple.asByteArray().length);

        Response tcomplex = get(scope, archive + "/schemas/0/tables/1/rows");
        tcomplex.then().statusCode(200);
        assertTrue(tcomplex.asByteArray().length < 20000, "TCOMPLEX body: " + tcomplex.asByteArray().length);
    }

    /**
     * Spec scenario "Data link cells show their text": as the desktop app, which reads them with
     * {@code getString()}; the fixture's files hold a {@code file://localhost/...} path as ASCII text.
     */
    @Test
    void dataLinkCellsShowTheirText() throws IOException {
        ArchiveScope scope = newScope();
        String table = tsimple(scope, fixture(DATALINK_FIXTURE));
        int column = column(scope, table, "COLUMN_DATALINK");
        String text = new String(CraftedArchive.entry(DATALINK_FIXTURE, DATALINK_FILE), StandardCharsets.UTF_8);
        assertTrue(text.startsWith("file://localhost/"), text);

        Response rows = get(scope, table + "/rows");
        rows.then().statusCode(200);
        List<Map<String, Object>> cells = rows.jsonPath().getList("rows.collect { it.cells[" + column + "] }");
        assertEquals(4, cells.size());
        for (Map<String, Object> cell : cells) {
            assertEquals(Map.of("type", "text", "value", text, "truncated", false), cell);
        }
        assertArrayEquals(text.getBytes(StandardCharsets.UTF_8), download(scope, table, column));
        JsonPath search = get(scope, table + "/search?q=file://localhost").jsonPath();
        assertEquals(true, search.getBoolean("found"));
        assertEquals(column, search.getInt("column"));
    }

    /**
     * Spec scenario "A data link that cannot be read is shown empty": the failure found with {@code test.siard}
     * (a JPEG as DATALINK file), whose character count {@code siard-api} rejects. The desktop app shows such a cell
     * empty; before, the whole page answered 500. Per cell: the other rows keep their text.
     */
    @Test
    void dataLinkThatCannotBeReadIsShownEmpty() throws IOException {
        byte[] jpegLike = new byte[2000];
        new Random(7).nextBytes(jpegLike);
        jpegLike[0] = (byte) 0xFF;
        jpegLike[1] = (byte) 0xD8;
        Path crafted = CraftedArchive.of(DATALINK_FIXTURE)
                .replaceLob(DATALINK_FILE, "content/schema0/table0/lob25/crafted0.bin", jpegLike)
                .writeTo(tmp);
        ArchiveScope scope = newScope();
        String table = tsimple(scope, crafted);
        int column = column(scope, table, "COLUMN_DATALINK");

        Response rows = get(scope, table + "/rows");
        rows.then().statusCode(200);
        assertEquals(EMPTY, rows.jsonPath().getMap("rows[0].cells[" + column + "]"));
        assertTrue(rows.jsonPath().getString("rows[1].cells[" + column + "].value").startsWith("file://localhost/"));
        assertArrayEquals(new byte[0], download(scope, table, column));
    }

    /**
     * Spec scenario "Content outside the archive is not followed": a cell with {@code dlurlpathonly} (a URL
     * instead of, or next to, a stored file; {@code metadata.xsd}) is shown empty and nothing opens the URL.
     * {@code siard-api} opens it before a stored file in {@code getInputStream()}, also for a BLOB, so the test
     * uses the BLOB column, and the case with both is the one a check for "no stored file" would miss.
     * {@code siard-api} validates rows against the archive's own table schema and never writes the attribute
     * there, so only a crafted archive whose {@code table0.xsd} allows it holds such a cell.
     */
    @ParameterizedTest(name = "stored file kept: {0}")
    @ValueSource(booleans = {false, true})
    void contentOutsideTheArchiveIsNotFollowed(boolean keepStoredFile) throws IOException {
        byte[] linked = new byte[1234];
        new Random(11).nextBytes(linked);
        String url = Files.write(tmp.resolve("linked.bin"), linked).toUri().toString();
        String blobFile = "content/schema0/table0/lob9/record0.bin";
        String stored = "<c10 digest=\"CBECBDB0FDD5CEC1E242493B6008CC79\" digestType=\"MD5\" file=\""
                + blobFile + "\" length=\"1000\"/>";
        CraftedArchive archive = CraftedArchive.of(DATALINK_FIXTURE)
                // allow the attribute on both LOB types of the table schema, as metadata.xsd does
                .replaceText(TSIMPLE_SCHEMA, "<xs:attribute name=\"digest\" type=\"xs:string\"/>",
                        "<xs:attribute name=\"digest\" type=\"xs:string\"/>"
                                + "<xs:attribute name=\"dlurlpathonly\" type=\"xs:anyURI\"/>")
                .replaceText(CraftedArchive.TSIMPLE, stored, keepStoredFile
                        ? stored.replace("/>", " dlurlpathonly=\"" + url + "\"/>")
                        : "<c10 dlurlpathonly=\"" + url + "\" length=\"" + linked.length + "\"/>");
        if (!keepStoredFile) {
            archive.remove(blobFile);
        }
        ArchiveScope scope = newScope();
        String table = tsimple(scope, archive.writeTo(tmp));
        int column = column(scope, table, "CBLOB");

        Response rows = get(scope, table + "/rows");
        rows.then().statusCode(200);
        assertEquals(EMPTY, rows.jsonPath().getMap("rows[0].cells[" + column + "]"));
        assertArrayEquals(new byte[0], download(scope, table, column));
        assertEquals(false, get(scope, table + "/search?q=linked.bin").jsonPath().getBoolean("found"));
    }

    /** Spec scenario "Cell values need a session": no scope, no cells and no cell content. */
    @Test
    void cellValuesNeedASession() throws IOException {
        String table = tsimple(newScope(), fixture("sample.siard"));

        for (String path : List.of(table + "/rows", table + "/rows/0/columns/0/content")) {
            Response response = given().get(path);
            response.then().statusCode(401).contentType(startsWith(ProblemDetail.MEDIA_TYPE));
            assertFalse(response.asString().contains("cells"), path);
        }
    }

    private String tsimple(ArchiveScope scope, Path archive) throws IOException {
        return "/api/archives/" + registry.register(scope, archive, () -> { }).value() + "/schemas/0/tables/0";
    }

    private static int column(ArchiveScope scope, String table, String name) {
        int column = get(scope, table).jsonPath().getList("columns.name").indexOf(name);
        assertTrue(column >= 0, "TSIMPLE has " + name);
        return column;
    }

    private static byte[] download(ArchiveScope scope, String table, int column) {
        return get(scope, table + "/rows/0/columns/" + column + "/content").then()
                .statusCode(200).extract().asByteArray();
    }
}
