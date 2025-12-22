package ch.admin.bar.siard2.cmd.mysql.issues.siardgui29;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SiardToDb;
import ch.admin.bar.siard2.cmd.SupportedDbVersions;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedColumnId;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

public class BitTypesIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MySQLContainer<?> emptyDb = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_5_6))
            .withUsername("root")
            .withPassword("public")
            .withDatabaseName("public")
            .withConfigurationOverride("mysql/config/with-blobs");

    @Rule
    public MySQLContainer<?> db = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_5_6))
            .withUsername("root")
            .withPassword("public")
            .withDatabaseName("public")
            .withInitScript(SqlScripts.MySQL.SIARDGUI_29_BIT)
            .withConfigurationOverride("mysql/config/with-blobs");

    //This test fails using the archive provided in the issue because typeOriginal BIT was converted to BIT(1)
    @Ignore
    @Test
    public void uploadSubmittedArchive_expectNoExceptions() throws SQLException, IOException {
        val submittedArchive = siardArchivesHandler.prepareResource("mysql/issues/siardgui29/submitted-bit-types.siard");

        SiardToDb siardToDb = new SiardToDb(new String[] {
                "-o",
                "-j:" + emptyDb.getJdbcUrl(),
                "-u:" + emptyDb.getUsername(),
                "-p:" + emptyDb.getPassword(),
                "-s:" + submittedArchive.getPathToArchiveFile()
        });
        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
    }

    @Test
    public void downloadArchive() throws SQLException, IOException, ClassNotFoundException {
        val siardArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb dbtoSiard = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + siardArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardFromDb.iRETURN_OK, dbtoSiard.getReturn());

        val metadataExplorer = siardArchive.exploreMetadata();

        val columnBit1 = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("bitschema"))
                .tableId(Id.of("bittest"))
                .columnId(Id.of("bit1"))
                .build());
        Assertions.assertThat(columnBit1.getType()).contains(Id.of("BOOLEAN"));
        Assertions.assertThat(columnBit1.getTypeOriginal()).contains(Id.of("bit(1)"));

        val columnBit8 = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("bitschema"))
                .tableId(Id.of("bittest"))
                .columnId(Id.of("bit8"))
                .build());
        Assertions.assertThat(columnBit8.getType()).contains(Id.of("BINARY(8)"));
        Assertions.assertThat(columnBit8.getTypeOriginal()).contains(Id.of("bit(8)"));

        val columnBit64 = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("bitschema"))
                .tableId(Id.of("bittest"))
                .columnId(Id.of("bit64"))
                .build());
        Assertions.assertThat(columnBit64.getType()).contains(Id.of("BINARY(64)"));
        Assertions.assertThat(columnBit64.getTypeOriginal()).contains(Id.of("bit(64)"));

    }
}
