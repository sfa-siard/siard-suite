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

public class BinaryTypesIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public OracleContainer db = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
            .withLogConsumer(new ConsoleLogConsumer())
            .withCopyFileToContainer(
                    MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.Oracle.CREATE_USER_WITH_ALL_PRIVILEGES).toPath()),
                    "/container-entrypoint-initdb.d/00_create_user.sql")
            .withCopyFileToContainer(
                    MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.Oracle.SIARDSUITE_129_BINARY).toPath()),
                    "/container-entrypoint-initdb.d/01_binary_types.sql");

    @Test
    public void downloadArchive() throws SQLException, IOException, ClassNotFoundException {
        val siardArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb dbtoSiard = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + "IT_USER",
                "-p:" + "password",
                "-s:" + siardArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardFromDb.iRETURN_OK, dbtoSiard.getReturn());

        val metadataExplorer = siardArchive.exploreMetadata();

        val columnId = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("ORACLE_BINARY_TEST"))
                .columnId(Id.of("ID"))
                .build());
        Assertions.assertThat(columnId.getType()).contains(Id.of("FLOAT(38)"));
        Assertions.assertThat(columnId.getTypeOriginal()).contains(Id.of("NUMBER"));

        val columnBitEquivalent = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("ORACLE_BINARY_TEST"))
                .columnId(Id.of("BIT_EQUIVALENT"))
                .build());
        Assertions.assertThat(columnBitEquivalent.getType()).contains(Id.of("SMALLINT"));
        Assertions.assertThat(columnBitEquivalent.getTypeOriginal()).contains(Id.of("NUMBER(1,0)"));

        val columnRawSmall = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("ORACLE_BINARY_TEST"))
                .columnId(Id.of("RAW_SMALL"))
                .build());
        Assertions.assertThat(columnRawSmall.getType()).contains(Id.of("VARBINARY(1)"));
        Assertions.assertThat(columnRawSmall.getTypeOriginal()).contains(Id.of("RAW(1)"));

        val columnRawMedium = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("ORACLE_BINARY_TEST"))
                .columnId(Id.of("RAW_MEDIUM"))
                .build());
        Assertions.assertThat(columnRawMedium.getType()).contains(Id.of("VARBINARY(8)"));
        Assertions.assertThat(columnRawMedium.getTypeOriginal()).contains(Id.of("RAW(8)"));

        val columnRawLarge = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("ORACLE_BINARY_TEST"))
                .columnId(Id.of("RAW_LARGE"))
                .build());
        Assertions.assertThat(columnRawLarge.getType()).contains(Id.of("VARBINARY(2000)"));
        Assertions.assertThat(columnRawLarge.getTypeOriginal()).contains(Id.of("RAW(2000)"));

        val columnBlobData = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("ORACLE_BINARY_TEST"))
                .columnId(Id.of("BLOB_DATA"))
                .build());
        Assertions.assertThat(columnBlobData.getType()).contains(Id.of("BLOB"));
        Assertions.assertThat(columnBlobData.getTypeOriginal()).contains(Id.of("BLOB"));
    }
}
