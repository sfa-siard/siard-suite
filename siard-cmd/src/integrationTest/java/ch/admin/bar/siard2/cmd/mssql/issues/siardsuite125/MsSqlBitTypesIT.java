package ch.admin.bar.siard2.cmd.mssql.issues.siardsuite125;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedColumnId;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.extension.RegisterExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class MsSqlBitTypesIT {

    @RegisterExtension
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Container
    public MSSQLServerContainer<?> db = new MSSQLServerContainer<>(DockerImageName.parse("mcr.microsoft.com/mssql/server:2017-CU12"))
            .acceptLicense()
            .withInitScript(SqlScripts.MsSQL.SIARDSUITE_125);

    @Test
    public void downloadArchive() throws SQLException, IOException, ClassNotFoundException {
        // given
        val siardArchive = siardArchivesHandler.prepareEmpty();

        // when
        SiardFromDb dbToSiard = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + siardArchive.getPathToArchiveFile()
        });

        // then
        assertEquals(SiardFromDb.iRETURN_OK, dbToSiard.getReturn());

        val metadataExplorer = siardArchive.exploreMetadata();

        val columnBit1 = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                                                                          .schemaId(Id.of("BitSchema"))
                                                                          .tableId(Id.of("BitTest"))
                                                                          .columnId(Id.of("bit1"))
                                                                          .build());
        Assertions.assertThat(columnBit1.getType())
                  .contains(Id.of("BOOLEAN"));
        Assertions.assertThat(columnBit1.getTypeOriginal())
                  .contains(Id.of("bit"));

        val columnBitArraySmall = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                                                                                   .schemaId(Id.of("BitSchema"))
                                                                                   .tableId(Id.of("BitTest"))
                                                                                   .columnId(Id.of("bit_array_small"))
                                                                                   .build());
        Assertions.assertThat(columnBitArraySmall.getType())
                  .contains(Id.of("BINARY(8)"));
        Assertions.assertThat(columnBitArraySmall.getTypeOriginal())
                  .contains(Id.of("binary(8)"));

        val columnBitArrayLarge = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                                                                                   .schemaId(Id.of("BitSchema"))
                                                                                   .tableId(Id.of("BitTest"))
                                                                                   .columnId(Id.of("bit_array_large"))
                                                                                   .build());
        Assertions.assertThat(columnBitArrayLarge.getType())
                  .contains(Id.of("BINARY(64)"));
        Assertions.assertThat(columnBitArrayLarge.getTypeOriginal())
                  .contains(Id.of("binary(64)"));
    }
}
