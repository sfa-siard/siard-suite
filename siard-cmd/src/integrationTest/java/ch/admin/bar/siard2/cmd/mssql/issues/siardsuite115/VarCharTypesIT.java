package ch.admin.bar.siard2.cmd.mssql.issues.siardsuite115;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedColumnId;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

public class VarCharTypesIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MSSQLServerContainer<?> db = new MSSQLServerContainer<>(DockerImageName.parse("mcr.microsoft.com/mssql/server:2017-CU12"))
            .acceptLicense()
            .withInitScript(SqlScripts.MsSQL.SIARDSUITE_115);

    @Test
    public void downloadArchive() throws SQLException, IOException, ClassNotFoundException {
        val siardArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb dbToSiard = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + siardArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardFromDb.iRETURN_OK, dbToSiard.getReturn());

       val metadataExplorer = siardArchive.exploreMetadata();

        val columnId = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("TestSchema"))
                .tableId(Id.of("VarCharTest"))
                .columnId(Id.of("Id"))
                .build());
        Assertions.assertThat(columnId.getType()).contains(Id.of("INT"));
        Assertions.assertThat(columnId.getTypeOriginal()).contains(Id.of("int"));

        val columnText1 = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("TestSchema"))
                .tableId(Id.of("VarCharTest"))
                .columnId(Id.of("text1"))
                .build());
        Assertions.assertThat(columnText1.getType()).contains(Id.of("VARCHAR(1)"));
        Assertions.assertThat(columnText1.getTypeOriginal()).contains(Id.of("varchar(1)"));

        val columnText2 = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("TestSchema"))
                .tableId(Id.of("VarCharTest"))
                .columnId(Id.of("text2"))
                .build());
        Assertions.assertThat(columnText2.getType()).contains(Id.of("VARCHAR(1)"));
        Assertions.assertThat(columnText2.getTypeOriginal()).contains(Id.of("varchar(1)"));

        val columnText3 = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("TestSchema"))
                .tableId(Id.of("VarCharTest"))
                .columnId(Id.of("text3"))
                .build());
        Assertions.assertThat(columnText3.getType()).contains(Id.of("VARCHAR(255)"));
        Assertions.assertThat(columnText3.getTypeOriginal()).contains(Id.of("varchar(255)"));

        val columnText4 = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("TestSchema"))
                .tableId(Id.of("VarCharTest"))
                .columnId(Id.of("text4"))
                .build());
        Assertions.assertThat(columnText4.getType()).contains(Id.of("VARCHAR(8000)"));
        Assertions.assertThat(columnText4.getTypeOriginal()).contains(Id.of("varchar(8000)"));

        val columnText5 = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("TestSchema"))
                .tableId(Id.of("VarCharTest"))
                .columnId(Id.of("text5"))
                .build());
        Assertions.assertThat(columnText5.getType()).contains(Id.of("VARCHAR(2147483647)"));
        Assertions.assertThat(columnText5.getTypeOriginal()).contains(Id.of("varchar(2147483647)"));
    }
}
