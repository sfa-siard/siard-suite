package ch.admin.bar.siardsuite.webcore.archive;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.Cell;
import ch.admin.bar.siard2.api.Table;
import ch.admin.bar.siard2.api.TableRecordDispenser;
import ch.admin.bar.siard2.api.facade.TableRecordFacade;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
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
import java.util.Map;

import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.fixture;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.get;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.newScope;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spec requirement "Cell values", on {@code sample.siard} and a crafted variant of it.
 */
@QuarkusTest
class CellValuesTest {

    /** The crafted text: {@code alpha42} at the start, {@code omega42} only at position 50000. */
    static final String LARGE_TEXT = "alpha42" + "x".repeat(50000 - 7) + "omega42" + "x".repeat(50000 - 7);
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
}
