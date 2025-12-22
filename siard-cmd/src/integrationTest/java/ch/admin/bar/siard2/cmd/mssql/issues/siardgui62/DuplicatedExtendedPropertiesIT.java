package ch.admin.bar.siard2.cmd.mssql.issues.siardgui62;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedTableId;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.StringWrapper;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

/**
 * See: https://github.com/sfa-siard/SiardGui/issues/62
 */
public class DuplicatedExtendedPropertiesIT {

    public final static String DUPLICATE_EXTENDED_PROPERTIES = "mssql/issues/siardgui62/duplicated-extended-properties.sql";

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MSSQLServerContainer db = new MSSQLServerContainer<>(DockerImageName.parse("mcr.microsoft.com/mssql/server:2017-CU12"))
            .acceptLicense()
            .withInitScript(DUPLICATE_EXTENDED_PROPERTIES);

    @Test
    public void shouldCreateSiardArchiveFromDb() throws SQLException, IOException, ClassNotFoundException {
        val actualArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + actualArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());
        actualArchive.preserveArchive();

        val metadataTable = actualArchive.exploreMetadata()
                .findByTableId(QualifiedTableId.builder()
                        .schemaId(Id.of("dbo"))
                        .tableId(Id.of("testtable"))
                        .build());
        Assertions.assertThat(metadataTable.getDescription().map(StringWrapper::getValue)).contains("Caption.| Description.");
    }
}
