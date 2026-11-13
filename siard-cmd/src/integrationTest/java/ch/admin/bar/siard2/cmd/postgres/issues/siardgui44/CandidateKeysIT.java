package ch.admin.bar.siard2.cmd.postgres.issues.siardgui44;

import ch.admin.bar.siard2.cmd.SiardCliTest;
import ch.admin.bar.siard2.cmd.utils.CollectionsHelper;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.header.Metadata;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedCandidateKeyId;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedColumnId;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedPrimaryKeyId;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedTableId;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class CandidateKeysIT {

    public final static Id<Metadata.Schema> SCHEMA = Id.of("siardgui44");
    public final static Id<Metadata.Table> TABLE_DOCUMENTS = Id.of("documents");

    public final static Id<Metadata.Column> COLUMN_DOCUMENT_ID = Id.of("document_id");
    public final static Id<Metadata.Column> COLUMN_DOCUMENT_NUMBER = Id.of("document_number");

    private static final QualifiedTableId DOCUMENTS = QualifiedTableId.builder()
                                                                     .schemaId(SCHEMA)
                                                                     .tableId(TABLE_DOCUMENTS)
                                                                     .build();

    private static final QualifiedPrimaryKeyId DOCUMENTS_PRIMARY_KEY = QualifiedPrimaryKeyId.builder()
                                                                                           .qualifiedTableId(DOCUMENTS)
                                                                                           .primaryKeyId(Id.of("pk_documents"))
                                                                                           .build();

    private static final QualifiedCandidateKeyId DOCUMENTS_CANDIDATE_KEY = QualifiedCandidateKeyId.builder()
                                                                                                 .qualifiedTableId(DOCUMENTS)
                                                                                                 .candidateKeyId(Id.of("uq_documents_document_number"))
                                                                                                 .build();

    @RegisterExtension
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Container
    public PostgreSQLContainer<?> db = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13"))
            .withInitScript(SqlScripts.Postgres.SIARDGUI_44_CANDIDATE_KEYS);

    @Test
    public void download_expectCandidateKeys() throws IOException, SQLException, ClassNotFoundException {
        // given
        val siardArchive = siardArchivesHandler.prepareEmpty();

        // when
        var siardFromDb = SiardCliTest.execute("archive",
                "--overwrite",
                "--jdbc-url=" + db.getJdbcUrl(),
                "--user=" + db.getUsername(),
                "--password=" + db.getPassword(),
                "--siard-file=" + siardArchive.getPathToArchiveFile());

        // then
        assertEquals(0, siardFromDb.exitCode());

        val metadataExplorer = siardArchive.exploreMetadata();

        Assertions.assertThat(metadataExplorer.tryFindPrimaryKey(DOCUMENTS))
                  .contains(Metadata.PrimaryKey.builder()
                                               .name(DOCUMENTS_PRIMARY_KEY.getPrimaryKeyId())
                                               .columns(CollectionsHelper.setOf(COLUMN_DOCUMENT_ID))
                                               .build());

        val candidateKey = metadataExplorer.findCandidateKey(DOCUMENTS_CANDIDATE_KEY);
        Assertions.assertThat(candidateKey.getColumns())
                  .isEqualTo(CollectionsHelper.setOf(COLUMN_DOCUMENT_NUMBER));

        metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                                                        .schemaId(SCHEMA)
                                                        .tableId(TABLE_DOCUMENTS)
                                                        .columnId(COLUMN_DOCUMENT_NUMBER)
                                                        .build());
    }
}
