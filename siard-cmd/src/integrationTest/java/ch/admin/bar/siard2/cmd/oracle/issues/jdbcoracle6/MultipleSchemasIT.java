package ch.admin.bar.siard2.cmd.oracle.issues.jdbcoracle6;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.utils.ConsoleLogConsumer;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.TestResourcesResolver;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedTableId;
import lombok.val;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;

import static org.assertj.core.api.Assertions.assertThat;

// tests download of multiple schemas
public class MultipleSchemasIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public final OracleContainer db = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
            .withLogConsumer(new ConsoleLogConsumer())
            .withCopyFileToContainer(
                    MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.Oracle.MULTPLE_SCHEMAS)
                                                                   .toPath()),
                    "/container-entrypoint-initdb.d/00_create_schemas.sql");

    // due to non-resolved issue https://github.com/sfa-siard/JdbcOracle/issues/10 expect an exception instead of ignoring the test.
    @Test(expected = SQLSyntaxErrorException.class)
    public void download() throws IOException, SQLException, ClassNotFoundException {
        // given
        val actualArchive = siardArchivesHandler.prepareEmpty();

        // when
        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + "testuser",
                "-p:" + "testpassword",
                "-s:" + actualArchive.getPathToArchiveFile()
        });

        // then
        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());

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
