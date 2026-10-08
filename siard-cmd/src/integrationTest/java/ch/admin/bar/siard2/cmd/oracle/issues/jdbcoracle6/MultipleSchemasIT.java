package ch.admin.bar.siard2.cmd.oracle.issues.jdbcoracle6;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.TestResourcesResolver;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedTableId;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.extension.RegisterExtension;

// tests download of multiple schemas
@Testcontainers
public class MultipleSchemasIT {

    @RegisterExtension
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Container
    public final OracleContainer db = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
            .withCopyFileToContainer(
                    MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.Oracle.MULTPLE_SCHEMAS)
                                                                   .toPath()),
                    "/container-entrypoint-initdb.d/00_create_schemas.sql");

    // due to non-resolved issue https://github.com/sfa-siard/JdbcOracle/issues/10 expect an exception instead of ignoring the test.
    @Test
    public void downloadMultipleSchemas() {
        // given
        val actualArchive = siardArchivesHandler.prepareEmpty();

        // when/then
        Assertions.assertThrows(SQLSyntaxErrorException.class, () -> new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + "testuser",
                "-p:" + "testpassword",
                "-s:" + actualArchive.getPathToArchiveFile()
        }));
    }

    @Test
    public void downloadSpecificSchema() throws IOException, SQLException, ClassNotFoundException {
        // given
        val actualArchive = siardArchivesHandler.prepareEmpty();

        // when
        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + "testuser",
                "-p:" + "testpassword",
                "--schema:" + "TESTUSER",
                "-s:" + actualArchive.getPathToArchiveFile()
        });

        // then
        Assertions.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());

        val metadataExplorer = actualArchive.exploreMetadata();

        assertThat(
                metadataExplorer.tryFindByTableId(QualifiedTableId.builder()
                                                                  .schemaId(Id.of("TESTUSER"))
                                                                  .tableId(Id.of("SIMPLE_TABLE"))
                                                                  .build()))
                .isPresent();

        assertThat(
                metadataExplorer.tryFindByTableId(QualifiedTableId.builder()
                                                                  .schemaId(Id.of("OTHERUSER"))
                                                                  .tableId(Id.of("SIMPLE_TABLE"))
                                                                  .build()))
                .isNotPresent();
    }
}
