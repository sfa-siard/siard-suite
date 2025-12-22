package ch.admin.bar.siard2.cmd.oracle.issues.siardsuite106;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SiardToDb;
import ch.admin.bar.siard2.cmd.utils.ConsoleLogConsumer;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.TestResourcesResolver;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedTableId;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.sql.SQLException;

public class SchemaNameUnderscoreIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public OracleContainer db = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
            .withLogConsumer(new ConsoleLogConsumer())
            .withCopyFileToContainer(
                    MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.Oracle.CREATE_USER_WITH_ALL_PRIVILEGES).toPath()),
                    "/container-entrypoint-initdb.d/00_create_user.sql")
            .withCopyFileToContainer(
                    MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.Oracle.SIARDSUITE_106_SCHEMA_NAME).toPath()),
                    "/container-entrypoint-initdb.d/schema-with-underscore.sql");

    @Test
    public void downloadArchive() throws SQLException, IOException, ClassNotFoundException {
        val siardArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + "IT_USER",
                "-p:" + "password",
                "-s:" + siardArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());

        val metadataExplorer = siardArchive.exploreMetadata();

        val testTable = metadataExplorer.findByTableId(QualifiedTableId.builder()
                .schemaId(Id.of("EMPLOYEE_DATA"))
                .tableId(Id.of("TEST_TABLE"))
                .build());
        Assertions.assertThat(testTable.getName()).isEqualTo(Id.of("TEST_TABLE"));

        val jobHistoryTable = metadataExplorer.findByTableId(QualifiedTableId.builder()
                .schemaId(Id.of("EMPLOYEE_DATA"))
                .tableId(Id.of("JOB_HISTORY"))
                .build());
        Assertions.assertThat(jobHistoryTable.getName()).isEqualTo(Id.of("JOB_HISTORY"));


        val employeesTable = metadataExplorer.findByTableId(QualifiedTableId.builder()
                .schemaId(Id.of("EMPLOYEE_DATA"))
                .tableId(Id.of("EMPLOYEES"))
                .build());
        Assertions.assertThat(employeesTable.getName()).isEqualTo(Id.of("EMPLOYEES"));
    }

    //The issue had reported an error when uploading back to db
    @Test
    public void uploadSiardArchive_expectNoExceptions() throws SQLException, IOException {
        val siardArchive = siardArchivesHandler.prepareResource("oracle/issues/siardsuite106/created-table-names-underscore-oracle.siard");

        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + "IT_USER",
                "-p:" + "password",
                "-s:" + siardArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
    }
}
