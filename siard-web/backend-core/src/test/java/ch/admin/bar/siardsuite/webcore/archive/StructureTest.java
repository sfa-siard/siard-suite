package ch.admin.bar.siardsuite.webcore.archive;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siard2.api.facade.MetaTableFacade;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siardsuite.webcore.error.ProblemDetail;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.fixture;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.get;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.newScope;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Spec requirements "Archive metadata" and "Schema, table and view structure", on {@code sample.siard}.
 */
@QuarkusTest
class StructureTest {

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
        return "/api/archives/" + registry.register(scope, file, () -> { }).value();
    }

    @Test
    void metadataOfTheSampleArchive() throws IOException {
        String archive = open(fixture("sample.siard"));

        get(scope, archive).then()
                .statusCode(200)
                .contentType(startsWith("application/json"))
                .body("dbName", equalTo("Sample Database"))
                .body("archiver", equalTo("Hartwig Thomas"))
                .body("archivalDate", equalTo("2018-02-01"))
                .body("schemas", hasSize(1))
                .body("schemas[0].index", equalTo(0))
                .body("schemas[0].name", equalTo("SampleSchema"));
    }

    @Test
    void unknownArchiveId() {
        get(newScope(), "/api/archives/" + ArchiveId.random().value()).then()
                .statusCode(404)
                .contentType(startsWith(ProblemDetail.MEDIA_TYPE));
    }

    @Test
    void writeMethodsAreRejected() throws IOException {
        Path file = fixture("sample.siard");
        long size = Files.size(file);
        FileTime mtime = Files.getLastModifiedTime(file);
        String archive = open(file);

        for (String method : List.of("PUT", "POST")) {
            given().header(TestScopeResolver.HEADER, scope.value()).contentType("application/json").body("{}")
                    .request(method, archive)
                    .then()
                    .statusCode(405)
                    .contentType(startsWith(ProblemDetail.MEDIA_TYPE));
        }
        assertEquals(size, Files.size(file));
        assertEquals(mtime, Files.getLastModifiedTime(file));
    }

    @Test
    void tablesOfASchema() throws IOException {
        String archive = open(fixture("sample.siard"));

        get(scope, archive + "/schemas/0").then()
                .statusCode(200)
                .body("tables.name", contains("TSIMPLE", "TCOMPLEX"))
                .body("tables.rows", contains(4, 2))
                .body("views.name", contains("VIEW1", "VIEW2"));
    }

    @Test
    void columnsOfATable() throws IOException {
        List<String> expected = new ArrayList<>();
        Archive reference = ArchiveImpl.newInstance();
        reference.openReadOnly(fixture("sample.siard").toFile());
        try {
            for (MetaColumn column : new MetaTableFacade(reference.getSchema(0).getTable(0).getMetaTable()).getMetaColums()) {
                expected.add(column.getName());
            }
        } finally {
            reference.close();
        }
        String archive = open(fixture("sample.siard"));

        Response response = get(scope, archive + "/schemas/0/tables/0");
        response.then().statusCode(200).body("rows", equalTo(4));
        assertEquals(expected, response.jsonPath().getList("columns.name"));
        for (Map<String, Object> column : response.jsonPath().<Map<String, Object>>getList("columns")) {
            assertFalse(((String) column.get("type")).isEmpty(), column.toString());
            assertInstanceOf(Boolean.class, column.get("nullable"), column.toString());
        }
    }

    @Test
    void indexOutOfRange() throws IOException {
        String archive = open(fixture("sample.siard"));

        for (String path : List.of("/schemas/7", "/schemas/0/tables/99", "/schemas/-1")) {
            get(scope, archive + path).then()
                    .statusCode(404)
                    .contentType(startsWith(ProblemDetail.MEDIA_TYPE));
        }
    }

    @Test
    void namesAreDataNeverPaths() throws IOException {
        String schemaName = "../x/y\\z";
        String tableName = "../TSIMPLE";
        Path crafted = CraftedArchive.of("sample.siard")
                .replaceText(CraftedArchive.METADATA, "<name>SampleSchema</name>", "<name>" + schemaName + "</name>")
                .replaceText(CraftedArchive.METADATA, "<name>TSIMPLE</name>", "<name>" + tableName + "</name>")
                .writeTo(tmp);
        String archive = open(crafted);

        get(scope, archive + "/schemas/0").then()
                .statusCode(200)
                .body("name", equalTo(schemaName))
                .body("tables[0].name", equalTo(tableName));
        get(scope, archive + "/schemas/0/tables/0").then()
                .statusCode(200)
                .body("name", equalTo(tableName));
    }
}
