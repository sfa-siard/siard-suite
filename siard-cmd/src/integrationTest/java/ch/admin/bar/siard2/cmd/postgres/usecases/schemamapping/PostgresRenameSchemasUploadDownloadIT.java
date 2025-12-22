package ch.admin.bar.siard2.cmd.postgres.usecases.schemamapping;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SiardToDb;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedTableId;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

public class PostgresRenameSchemasUploadDownloadIT {

    /**
     * The file contains two schemes (Schema1 and Schema2) and each of it contains two tables
     * (Schema1.Table1, Schema1.Table2, Schema2.Table3 and Schema2.Table4).
     * </p>
     * See usecases/schemamapping/create-multiple-schemas-with-multiple-tables_postgres.sql (Script which is used in
     * {@link PostgresDownloadMultiSchemaSiardProjectIT} for generating the SIARD archive)
     */
    public final static String SIARD_FILE = "postgres/usecases/schemamapping/multiple-schemas-with-multiple-tables.siard";

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public PostgreSQLContainer<?> db = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13"));

    @Test
    public void uploadAndDownload_expectNoExceptions() throws IOException, SQLException, ClassNotFoundException {
        // given
        val expectedArchive = siardArchivesHandler.prepareResource(SIARD_FILE);
        val actualArchive = siardArchivesHandler.prepareEmpty();

        // when
        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + expectedArchive.getPathToArchiveFile(),
                "schema1", "editedSchema1",
                "schema2", "editedSchema2"
        });
        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + actualArchive.getPathToArchiveFile()
        });

        // then
        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());

        val metadataExplorer = actualArchive.exploreMetadata();

        Assertions.assertThat(metadataExplorer.tryFindByTableId(QualifiedTableId.builder()
                        .schemaId(Id.of("editedSchema1"))
                        .tableId(Id.of("table1"))
                        .build()))
                .isPresent();

        Assertions.assertThat(metadataExplorer.tryFindByTableId(QualifiedTableId.builder()
                        .schemaId(Id.of("editedSchema1"))
                        .tableId(Id.of("table2"))
                        .build()))
                .isPresent();

        Assertions.assertThat(metadataExplorer.tryFindByTableId(QualifiedTableId.builder()
                        .schemaId(Id.of("editedSchema2"))
                        .tableId(Id.of("table3"))
                        .build()))
                .isPresent();

        Assertions.assertThat(metadataExplorer.tryFindByTableId(QualifiedTableId.builder()
                        .schemaId(Id.of("editedSchema2"))
                        .tableId(Id.of("table4"))
                        .build()))
                .isPresent();

        // TODO: Extend for types...
    }
}
