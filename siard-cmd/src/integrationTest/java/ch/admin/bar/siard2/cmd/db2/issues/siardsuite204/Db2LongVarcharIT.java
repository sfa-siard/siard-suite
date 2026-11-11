package ch.admin.bar.siard2.cmd.db2.issues.siardsuite204;

import ch.admin.bar.siard2.cmd.SiardCliTest;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedColumnId;
import lombok.val;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Db2Container;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.extension.RegisterExtension;

@Testcontainers
public class Db2LongVarcharIT {

    @RegisterExtension
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Container
    public Db2Container db = new Db2Container(DockerImageName.parse("ibmcom/db2:11.5.8.0"))
            .acceptLicense()
            .withInitScript(SqlScripts.Db2.SIARDSUITE_204_LONG_VARCHAR);

    @Test
    public void downloadArchive() throws SQLException, IOException, ClassNotFoundException {
        val siardArchive = siardArchivesHandler.prepareEmpty();

        var dbToSiard = SiardCliTest.execute("archive",
                "--overwrite",
                "--jdbc-url=" + db.getJdbcUrl(),
                "--user=" + db.getUsername(),
                "--password=" + db.getPassword(),
                "--siard-file=" + siardArchive.getPathToArchiveFile());

        Assertions.assertEquals(0, dbToSiard.exitCode());

        val metadataExplorer = siardArchive.exploreMetadata();
        val softly = new SoftAssertions();

        // LONG VARCHAR
        val longVarcharCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("LONG_VARCHAR_TEST"))
                .columnId(Id.of("LONG_VARCHAR_COL"))
                .build());
        softly.assertThat(longVarcharCol.getType()).contains(Id.of("VARCHAR(32700)"));
        softly.assertThat(longVarcharCol.getTypeOriginal()).contains(Id.of("LONG VARCHAR"));

        // LONG VARCHAR FOR BIT DATA
        val longVarcharBitCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("LONG_VARCHAR_TEST"))
                .columnId(Id.of("LONG_VARCHAR_BIT_COL"))
                .build());
        softly.assertThat(longVarcharBitCol.getType()).contains(Id.of("VARBINARY(32700)"));
        softly.assertThat(longVarcharBitCol.getTypeOriginal()).contains(Id.of("LONG VARCHAR FOR BIT DATA"));

        softly.assertAll();
    }
}
