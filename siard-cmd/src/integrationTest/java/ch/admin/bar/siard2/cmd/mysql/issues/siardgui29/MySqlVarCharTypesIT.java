package ch.admin.bar.siard2.cmd.mysql.issues.siardgui29;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SupportedDbVersions;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedColumnId;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

public class MySqlVarCharTypesIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MySQLContainer<?> db = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_5_6))
            .withUsername("root")
            .withPassword("public")
            .withDatabaseName("public")
            .withInitScript(SqlScripts.MySQL.SIARDGUI_29_VARCHAR)
            .withConfigurationOverride("mysql/config/with-blobs");

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
                .schemaId(Id.of("varcharschema"))
                .tableId(Id.of("varchartest"))
                .columnId(Id.of("id"))
                .build());
        Assertions.assertThat(columnId.getType()).contains(Id.of("INT"));
        Assertions.assertThat(columnId.getTypeOriginal()).contains(Id.of("int(11)"));

        val columnText2 = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("varcharschema"))
                .tableId(Id.of("varchartest"))
                .columnId(Id.of("text2"))
                .build());
        Assertions.assertThat(columnText2.getType()).contains(Id.of("VARCHAR(1)"));
        Assertions.assertThat(columnText2.getTypeOriginal()).contains(Id.of("varchar(1)"));

        val columnText3 = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("varcharschema"))
                .tableId(Id.of("varchartest"))
                .columnId(Id.of("text3"))
                .build());
        Assertions.assertThat(columnText3.getType()).contains(Id.of("VARCHAR(255)"));
        Assertions.assertThat(columnText3.getTypeOriginal()).contains(Id.of("varchar(255)"));

        val columnText4 = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("varcharschema"))
                .tableId(Id.of("varchartest"))
                .columnId(Id.of("text4"))
                .build());
        Assertions.assertThat(columnText4.getType()).contains(Id.of("VARCHAR(8000)"));
        Assertions.assertThat(columnText4.getTypeOriginal()).contains(Id.of("varchar(8000)"));
    }
}
