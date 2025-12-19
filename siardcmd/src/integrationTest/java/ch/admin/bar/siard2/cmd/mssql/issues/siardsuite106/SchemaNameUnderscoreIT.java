package ch.admin.bar.siard2.cmd.mssql.issues.siardsuite106;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SiardToDb;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedTableId;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

public class SchemaNameUnderscoreIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MSSQLServerContainer<?> db = new MSSQLServerContainer<>(DockerImageName.parse("mcr.microsoft.com/mssql/server:2017-CU12"))
            .acceptLicense()
            .withInitScript(SqlScripts.MsSQL.SIARDSUITE_106_SCHEMA_NAME);

    @Test
    public void downloadArchive() throws SQLException, IOException, ClassNotFoundException {
        val siardArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + siardArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());

        val metadataExplorer = siardArchive.exploreMetadata();

        val testTable = metadataExplorer.findByTableId(QualifiedTableId.builder()
                .schemaId(Id.of("employee_data"))
                .tableId(Id.of("test_table"))
                .build());
        Assertions.assertThat(testTable.getName()).isEqualTo(Id.of("test_table"));

        val jobHistoryTable = metadataExplorer.findByTableId(QualifiedTableId.builder()
                .schemaId(Id.of("employee_data"))
                .tableId(Id.of("job_history"))
                .build());
        Assertions.assertThat(jobHistoryTable.getName()).isEqualTo(Id.of("job_history"));


        val employeesTable = metadataExplorer.findByTableId(QualifiedTableId.builder()
                .schemaId(Id.of("employee_data"))
                .tableId(Id.of("employees"))
                .build());
        Assertions.assertThat(employeesTable.getName()).isEqualTo(Id.of("employees"));
    }

    //The issue had reported an error when uploading back to db
    @Test
    public void uploadSiardArchive_expectNoExceptions() throws SQLException, IOException {
        val siardArchive = siardArchivesHandler.prepareResource("mssql/issues/siardsuite106/created-table-names-underscore-mssql.siard");

        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + siardArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
    }
}
