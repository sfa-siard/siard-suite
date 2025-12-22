package ch.admin.bar.siard2.cmd.mssql.issues.siardcmd25;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedColumnId;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedTypeId;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

public class DoubleQuotesInUserDefinedTypesIT {

    public final static String CREATE_TABLE_WITH_CUSTOM_TYPE = "mssql/issues/siardcmd25/create-table-with-custom-type.sql";

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MSSQLServerContainer db = new MSSQLServerContainer<>(DockerImageName.parse("mcr.microsoft.com/mssql/server:2017-CU12"))
            .acceptLicense()
            .withInitScript(CREATE_TABLE_WITH_CUSTOM_TYPE);

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

        val metadataExplorer = actualArchive.exploreMetadata();

        val column = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("dbo"))
                .tableId(Id.of("testtable"))
                .columnId(Id.of("DAY"))
                .build());

        Assertions.assertThat(column.getTypeName()).contains(Id.of("dtDay2"));

        val type = metadataExplorer.tryFindByTypeId(QualifiedTypeId.builder()
                .schemaId(Id.of("dbo"))
                .typeId(Id.of("dtDay2"))
                .build());

        Assertions.assertThat(type).isPresent();
    }
}
