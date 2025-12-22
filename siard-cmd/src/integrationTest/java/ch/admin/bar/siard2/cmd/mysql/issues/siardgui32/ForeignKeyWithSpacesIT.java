package ch.admin.bar.siard2.cmd.mysql.issues.siardgui32;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SiardToDb;
import ch.admin.bar.siard2.cmd.SupportedDbVersions;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedColumnId;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedForeignKeyId;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

public class ForeignKeyWithSpacesIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MySQLContainer<?> emptyDb = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_5_6))
            .withUsername("root")
            .withPassword("public")
            .withDatabaseName("public")
            .withConfigurationOverride("mysql/config/with-blobs");

    @Rule
    public MySQLContainer<?> db = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_5_6))
            .withUsername("root")
            .withPassword("public")
            .withDatabaseName("public")
            .withInitScript(SqlScripts.MySQL.SIARDGUI_32_FOREIGN_KEY)
            .withConfigurationOverride("mysql/config/with-blobs");


    @Test
    public void uploadSubmittedArchive_expectNoExceptions() throws SQLException, IOException {
        val submittedArchive = siardArchivesHandler.prepareResource("mysql/issues/siardgui32/submitted-foreign-key.siard");

            SiardToDb siardToDb = new SiardToDb(new String[] {
                    "-o",
                    "-j:" + emptyDb.getJdbcUrl(),
                    "-u:" + emptyDb.getUsername(),
                    "-p:" + emptyDb.getPassword(),
                    "-s:" + submittedArchive.getPathToArchiveFile()
            });

        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
    }

    @Test
    public void downloadArchive() throws SQLException, IOException, ClassNotFoundException {

        val siardArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + siardArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());

        val metadataExplorer = siardArchive.exploreMetadata();

        val ordersOrderId = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("simpledb"))
                .tableId(Id.of("orders"))
                .columnId(Id.of("OrderID"))
                .build());
        Assertions.assertThat(ordersOrderId.getType()).contains(Id.of("INT"));
        Assertions.assertThat(ordersOrderId.getTypeOriginal()).contains(Id.of("int(11)"));

        val ordersCustomerName = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("simpledb"))
                .tableId(Id.of("orders"))
                .columnId(Id.of("CustomerName"))
                .build());
        Assertions.assertThat(ordersCustomerName.getType()).contains(Id.of("VARCHAR(100)"));
        Assertions.assertThat(ordersCustomerName.getTypeOriginal()).contains(Id.of("varchar(100)"));

        val detailsOrderId = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("simpledb"))
                .tableId(Id.of("order_details"))
                .columnId(Id.of("OrderID"))
                .build());
        Assertions.assertThat(detailsOrderId.getType()).contains(Id.of("INT"));
        Assertions.assertThat(detailsOrderId.getTypeOriginal()).contains(Id.of("int(11)"));

        val foreignKey = metadataExplorer.findForeignKey(QualifiedForeignKeyId.builder()
                .schemaId(Id.of("simpledb"))
                .tableId(Id.of("order_details"))
                .foreignKeyId(Id.of("Orders Order Details"))
                .build());
        Assertions.assertThat(foreignKey.getReferencedSchema()).isEqualTo(Id.of("simpledb"));
        Assertions.assertThat(foreignKey.getReferencedTable()).isEqualTo(Id.of("orders"));
    }
}