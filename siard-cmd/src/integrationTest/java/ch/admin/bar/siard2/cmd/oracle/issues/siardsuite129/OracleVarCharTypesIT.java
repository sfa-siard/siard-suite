package ch.admin.bar.siard2.cmd.oracle.issues.siardsuite129;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.utils.ConsoleLogConsumer;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.TestResourcesResolver;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedColumnId;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.sql.SQLException;

public class OracleVarCharTypesIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public OracleContainer db = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
            .withLogConsumer(new ConsoleLogConsumer())
            .withCopyFileToContainer(
                    MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.Oracle.CREATE_USER_WITH_ALL_PRIVILEGES).toPath()),
                    "/container-entrypoint-initdb.d/00_create_user.sql")
            .withCopyFileToContainer(
                    MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.Oracle.SIARDSUITE_129_VARCHAR).toPath()),
                    "/container-entrypoint-initdb.d/01_varchar_types.sql");

    @Test
    public void downloadArchive() throws SQLException, IOException, ClassNotFoundException {
        val siardArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb dbToSiard = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + "IT_USER",
                "-p:" + "password",
                "-s:" + siardArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardFromDb.iRETURN_OK, dbToSiard.getReturn());

        val metadataExplorer = siardArchive.exploreMetadata();

        val columnId = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("VARCHARTEST"))
                .columnId(Id.of("ID"))
                .build());
        Assertions.assertThat(columnId.getType()).contains(Id.of("FLOAT(38)"));
        Assertions.assertThat(columnId.getTypeOriginal()).contains(Id.of("NUMBER"));

        val columnText2 = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("VARCHARTEST"))
                .columnId(Id.of("TEXT2"))
                .build());
        Assertions.assertThat(columnText2.getType()).contains(Id.of("VARCHAR(1)"));
        Assertions.assertThat(columnText2.getTypeOriginal()).contains(Id.of("VARCHAR2(1)"));

        val columnText3 = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("VARCHARTEST"))
                .columnId(Id.of("TEXT3"))
                .build());
        Assertions.assertThat(columnText3.getType()).contains(Id.of("VARCHAR(255)"));
        Assertions.assertThat(columnText3.getTypeOriginal()).contains(Id.of("VARCHAR2(255)"));

        val columnText4 = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("VARCHARTEST"))
                .columnId(Id.of("TEXT4"))
                .build());
        Assertions.assertThat(columnText4.getType()).contains(Id.of("VARCHAR(4000)"));
        Assertions.assertThat(columnText4.getTypeOriginal()).contains(Id.of("VARCHAR2(4000)"));

        val columnDayOfYear = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("VARCHARTEST"))
                .columnId(Id.of("DAY_OF_YEAR"))
                .build());
        Assertions.assertThat(columnDayOfYear.getType()).contains(Id.of("SMALLINT"));
        Assertions.assertThat(columnDayOfYear.getTypeOriginal()).contains(Id.of("NUMBER(3,0)"));

        val columnWeightInKg = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("VARCHARTEST"))
                .columnId(Id.of("WEIGHT_IN_KG"))
                .build());
        Assertions.assertThat(columnWeightInKg.getType()).contains(Id.of("DEC(3, 2)"));
        Assertions.assertThat(columnWeightInKg.getTypeOriginal()).contains(Id.of("NUMBER(3,2)"));
    }
}
