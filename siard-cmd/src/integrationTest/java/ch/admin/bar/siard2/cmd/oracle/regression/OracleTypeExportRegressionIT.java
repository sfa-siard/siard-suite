package ch.admin.bar.siard2.cmd.oracle.regression;

import ch.admin.bar.siard2.cmd.SiardCliTest;
import ch.admin.bar.siard2.cmd.utils.OracleTestDb;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.TestResourcesResolver;
import ch.admin.bar.siard2.cmd.utils.siard.model.SiardArchive;
import ch.admin.bar.siard2.cmd.utils.siard.model.content.ContentReader;
import ch.admin.bar.siard2.cmd.utils.siard.model.header.MetadataReader;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedColumnId;
import ch.admin.bar.siard2.cmd.utils.siard.utils.MetadataExplorer;
import ch.admin.bar.siard2.cmd.utils.siard.utils.Unzipper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OracleTypeExportRegressionIT {

    @Container
    static final OracleContainer database = OracleTestDb.newContainer()
            .withCopyFileToContainer(
                    resource(SqlScripts.Oracle.CREATE_USER_WITH_ALL_PRIVILEGES),
                    "/container-entrypoint-initdb.d/00_create_user.sql")
            .withCopyFileToContainer(
                    resource(SqlScripts.Oracle.SIARDSUITE_129_BINARY),
                    "/container-entrypoint-initdb.d/01_binary_types.sql")
            .withCopyFileToContainer(
                    resource(SqlScripts.Oracle.SIARDSUITE_129_VARCHAR),
                    "/container-entrypoint-initdb.d/02_varchar_types.sql");

    @TempDir
    static Path temporaryDirectory;

    private MetadataExplorer metadata;

    @BeforeAll
    void archiveFixture() throws IOException {
        var archive = temporaryDirectory.resolve("type-export.siard");
        var result = SiardCliTest.execute("archive",
                "--overwrite",
                "--jdbc-url=" + database.getJdbcUrl(),
                "--user=IT_USER",
                "--password=password",
                "--siard-file=" + archive);
        assertEquals(0, result.exitCode(), "Composed Oracle fixture must archive successfully");

        var extracted = Files.createDirectory(temporaryDirectory.resolve("extracted")).toFile();
        new Unzipper(archive.toFile(), extracted).unzip();
        metadata = new MetadataExplorer(new SiardArchive(
                new MetadataReader(extracted).read(), new ContentReader(extracted).read()));
    }

    @Test
    void binaryTypeBoundaries_siardsuite129() {
        var table = "ORACLE_BINARY_TEST";
        assertAll("siardsuite129 binary type boundaries",
                columnTypes(table, "ID", "FLOAT(38)", "NUMBER"),
                columnTypes(table, "BIT_EQUIVALENT", "SMALLINT", "NUMBER(1,0)"),
                columnTypes(table, "RAW_SMALL", "VARBINARY(1)", "RAW(1)"),
                columnTypes(table, "RAW_MEDIUM", "VARBINARY(8)", "RAW(8)"),
                columnTypes(table, "RAW_LARGE", "VARBINARY(2000)", "RAW(2000)"),
                columnTypes(table, "BLOB_DATA", "BLOB", "BLOB"));
    }

    @Test
    void varcharTypeBoundaries_siardsuite129() {
        var table = "VARCHARTEST";
        assertAll("siardsuite129 VARCHAR type boundaries",
                columnTypes(table, "ID", "FLOAT(38)", "NUMBER"),
                columnTypes(table, "TEXT2", "VARCHAR(1)", "VARCHAR2(1)"),
                columnTypes(table, "TEXT3", "VARCHAR(255)", "VARCHAR2(255)"),
                columnTypes(table, "TEXT4", "VARCHAR(4000)", "VARCHAR2(4000)"),
                columnTypes(table, "DAY_OF_YEAR", "SMALLINT", "NUMBER(3,0)"),
                columnTypes(table, "WEIGHT_IN_KG", "DEC(3, 2)", "NUMBER(3,2)"));
    }

    private Executable columnTypes(String table, String column, String type, String originalType) {
        return () -> {
            var qualifiedColumn = QualifiedColumnId.builder()
                    .schemaId(Id.of("IT_USER"))
                    .tableId(Id.of(table))
                    .columnId(Id.of(column))
                    .build();
            var actual = metadata.findByColumnId(qualifiedColumn);
            var name = "IT_USER." + table + "." + column;
            assertAll(name,
                    () -> assertThat(actual.getType()).as("%s normalized type", name).contains(Id.of(type)),
                    () -> assertThat(actual.getTypeOriginal()).as("%s original type", name).contains(Id.of(originalType)));
        };
    }

    private static MountableFile resource(String path) {
        return MountableFile.forHostPath(TestResourcesResolver.resolve(path).toPath());
    }
}
