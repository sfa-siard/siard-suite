package ch.admin.bar.siard2.cmd.postgres.issues.multipleschemas;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedTableId;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedViewId;
import lombok.val;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

// Reproduces the issue where a restricted user can only access a view schema (views_schema)
// but SIARD tries to access all schemas (including data schemas s1, s2) and fails.
// The test connects as a restricted user (siard_user) that only has access to views_schema
// and uses the --schema option to export only that schema.
public class MultipleSchemasPostgresIT {

    private static final String SIARD_USER = "siard_user";
    private static final String SIARD_PASSWORD = "siard_password";

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public PostgreSQLContainer<?> db = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13"))
            .withInitScript(SqlScripts.Postgres.MULTIPLE_SCHEMAS);

    @Test
    public void download_withSchemaFilter_restrictedUserShouldOnlyArchiveViewSchema() throws IOException, SQLException, ClassNotFoundException {
        // given
        val actualArchive = siardArchivesHandler.prepareEmpty();

        // Connect as the restricted user (siard_user) who can only access views_schema
        // when
        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "--schema:" + "views_schema",
                "-s:" + actualArchive.getPathToArchiveFile()
        });

        // then
        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());

        val metadataExplorer = actualArchive.exploreMetadata();

        assertThat(
                metadataExplorer.tryFindByViewId(QualifiedViewId.builder()
                        .schemaId(Id.of("views_schema"))
                        .viewId(Id.of("v_customers"))
                        .build()))
                .isPresent();

        assertThat(
                metadataExplorer.tryFindByViewId(QualifiedViewId.builder()
                        .schemaId(Id.of("views_schema"))
                        .viewId(Id.of("v_orders"))
                        .build()))
                .isPresent();

        assertThat(
                metadataExplorer.tryFindByTableId(QualifiedTableId.builder()
                        .schemaId(Id.of("s1"))
                        .tableId(Id.of("customers"))
                        .build()))
                .isNotPresent();

        assertThat(
                metadataExplorer.tryFindByTableId(QualifiedTableId.builder()
                        .schemaId(Id.of("s2"))
                        .tableId(Id.of("orders"))
                        .build()))
                .isNotPresent();
    }
}
