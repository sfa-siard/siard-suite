package ch.admin.bar.siard2.cmd.postgres.issues.siardsuite128;

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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

public class BitTypesPostgresIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public PostgreSQLContainer<?> db = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13"))
            .withInitScript(SqlScripts.Postgres.SIARDSUITE_128_BIT);

    @Test
    public void downloadArchive() throws IOException, SQLException, ClassNotFoundException {
        // given
        val siardArchive = siardArchivesHandler.prepareEmpty();

        // when
        SiardFromDb dbtoSiard = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + siardArchive.getPathToArchiveFile()
        });

        // then
        Assert.assertEquals(SiardFromDb.iRETURN_OK, dbtoSiard.getReturn());

        val metadataExplorer = siardArchive.exploreMetadata();

        val columnId = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("bitschema"))
                .tableId(Id.of("bittest"))
                .columnId(Id.of("id"))
                .build());
        Assertions.assertThat(columnId.getType()).contains(Id.of("INT"));
        Assertions.assertThat(columnId.getTypeOriginal()).contains(Id.of("int4"));

        val columnBit1 = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("bitschema"))
                .tableId(Id.of("bittest"))
                .columnId(Id.of("bit1"))
                .build());
        Assertions.assertThat(columnBit1.getType()).contains(Id.of("BINARY(1)"));
        Assertions.assertThat(columnBit1.getTypeOriginal()).contains(Id.of("bit_1"));

        val columnBit8 = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("bitschema"))
                .tableId(Id.of("bittest"))
                .columnId(Id.of("bit8"))
                .build());
        Assertions.assertThat(columnBit8.getType()).contains(Id.of("BINARY(8)"));
        Assertions.assertThat(columnBit8.getTypeOriginal()).contains(Id.of("bit_8"));

        val columnBit64 = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("bitschema"))
                .tableId(Id.of("bittest"))
                .columnId(Id.of("bit64"))
                .build());
        Assertions.assertThat(columnBit64.getType()).contains(Id.of("BINARY(64)"));
        Assertions.assertThat(columnBit64.getTypeOriginal()).contains(Id.of("bit_64"));

    }
}
