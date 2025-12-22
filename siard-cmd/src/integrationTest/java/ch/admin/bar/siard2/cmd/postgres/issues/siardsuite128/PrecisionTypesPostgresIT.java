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

public class PrecisionTypesPostgresIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public PostgreSQLContainer<?> db = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13"))
            .withInitScript(SqlScripts.Postgres.SIARDSUITE_128_PRECISION_TYPES);

    @Test
    public void downloadArchive() throws SQLException, IOException, ClassNotFoundException {
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
                .schemaId(Id.of("precisiontypesschema"))
                .tableId(Id.of("typed_precision_test"))
                .columnId(Id.of("id"))
                .build());
        Assertions.assertThat(columnId.getType()).contains(Id.of("INT"));
        Assertions.assertThat(columnId.getTypeOriginal()).contains(Id.of("int4"));

        val columnVarchar_1 = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("precisiontypesschema"))
                .tableId(Id.of("typed_precision_test"))
                .columnId(Id.of("col_varchar_1"))
                .build());
        Assertions.assertThat(columnVarchar_1.getType()).contains(Id.of("VARCHAR(1)"));
        Assertions.assertThat(columnVarchar_1.getTypeOriginal()).contains(Id.of("varchar_1"));

        val columnVarchar_255 = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("precisiontypesschema"))
                .tableId(Id.of("typed_precision_test"))
                .columnId(Id.of("col_varchar_255"))
                .build());
        Assertions.assertThat(columnVarchar_255.getType()).contains(Id.of("VARCHAR(255)"));
        Assertions.assertThat(columnVarchar_255.getTypeOriginal()).contains(Id.of("varchar_255"));

        val columnVarchar_8000 = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("precisiontypesschema"))
                .tableId(Id.of("typed_precision_test"))
                .columnId(Id.of("col_varchar_8000"))
                .build());
        Assertions.assertThat(columnVarchar_8000.getType()).contains(Id.of("VARCHAR(8000)"));
        Assertions.assertThat(columnVarchar_8000.getTypeOriginal()).contains(Id.of("varchar_8000"));

        val columnText = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("precisiontypesschema"))
                .tableId(Id.of("typed_precision_test"))
                .columnId(Id.of("col_text"))
                .build());
        Assertions.assertThat(columnText.getType()).contains(Id.of("VARCHAR(10485760)"));
        Assertions.assertThat(columnText.getTypeOriginal()).contains(Id.of("text"));

        val columnChar10 = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("precisiontypesschema"))
                .tableId(Id.of("typed_precision_test"))
                .columnId(Id.of("col_char_10"))
                .build());
        Assertions.assertThat(columnChar10.getType()).contains(Id.of("CHAR(10)"));
        Assertions.assertThat(columnChar10.getTypeOriginal()).contains(Id.of("bpchar_10"));

        val columnNumeric_10_2 = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("precisiontypesschema"))
                .tableId(Id.of("typed_precision_test"))
                .columnId(Id.of("col_numeric_10_2"))
                .build());
        Assertions.assertThat(columnNumeric_10_2.getType()).contains(Id.of("NUMERIC(10, 2)"));
        Assertions.assertThat(columnNumeric_10_2.getTypeOriginal()).contains(Id.of("numeric_10_2"));

        val columnNumeric_8 = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("precisiontypesschema"))
                .tableId(Id.of("typed_precision_test"))
                .columnId(Id.of("col_numeric_8"))
                .build());
        Assertions.assertThat(columnNumeric_8.getType()).contains(Id.of("NUMERIC(8)"));
        Assertions.assertThat(columnNumeric_8.getTypeOriginal()).contains(Id.of("numeric_8"));
    }
}
