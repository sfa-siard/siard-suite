package ch.admin.bar.siard2.cmd.postgres.issues.multipleschemas;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedTableId;
import lombok.val;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

// tests download of a single schema from a database with multiple schemas using the -n option
public class MultipleSchemasPostgresIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public PostgreSQLContainer<?> db = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13"))
            .withInitScript(SqlScripts.Postgres.MULTIPLE_SCHEMAS);

    @Test
    public void download_withSchemaFilter_shouldOnlyArchiveSpecifiedSchema() throws IOException, SQLException, ClassNotFoundException {
        // given
        val actualArchive = siardArchivesHandler.prepareEmpty();

        // when
        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "--schema:" + "schema1",
                "-s:" + actualArchive.getPathToArchiveFile()
        });

        // then
        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());

        val metadataExplorer = actualArchive.exploreMetadata();

        assertThat(
                metadataExplorer.tryFindByTableId(QualifiedTableId.builder()
                        .schemaId(Id.of("schema1"))
                        .tableId(Id.of("simple_table"))
                        .build()))
                .isPresent();

        assertThat(
                metadataExplorer.tryFindByTableId(QualifiedTableId.builder()
                        .schemaId(Id.of("schema2"))
                        .tableId(Id.of("simple_table"))
                        .build()))
                .isNotPresent();

        assertThat(
                metadataExplorer.tryFindByTableId(QualifiedTableId.builder()
                        .schemaId(Id.of("public"))
                        .tableId(Id.of("simple_table"))
                        .build()))
                .isNotPresent();
    }
}
