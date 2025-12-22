package ch.admin.bar.siard2.cmd.mysql.usecases.keys.download;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.utils.CollectionsHelper;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.header.Metadata;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedColumnId;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedForeignKeyId;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedPrimaryKeyId;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedTableId;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.StringWrapper;
import lombok.SneakyThrows;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Assert;

import java.util.Optional;

public class MySqlKeysDownload {

    /**
     * +-------------------+          +-------------------+          +---------------------+
     * |      Schema1      |          |      Schema2      |          |       Schema2       |
     * +-------------------+          +-------------------+          +---------------------+
     * |                   |          |                   |          |                     |
     * |      Teams        |          |      Members      |          |     TeamMembers     |
     * |-------------------|          |-------------------|          |---------------------|
     * | TeamName (PK)     |          | MemberID (PK)     |          | TeamMembersID (PK)  |
     * | Location (PK)     |          | MemberName        |          | TeamName (FK)       |
     * |                   |          |                   |          | Location (FK)       |
     * |                   |          |                   |          | MemberID (FK)       |
     * +-------------------+          +-------------------+          +---------------------+
     */
    public final static String INIT_SCRIPT = "mysql/usecases/keys/download/create-simple-teams-example_mysql.sql";

    public final static Id<Metadata.Schema> SCHEMA_1 = Id.of("Schema1");
    public final static Id<Metadata.Schema> SCHEMA_2 = Id.of("Schema2");

    public final static Id<Metadata.Table> TABLE_TEAMS = Id.of("Teams");
    public final static Id<Metadata.Table> TABLE_MEMBERS = Id.of("Members");
    public final static Id<Metadata.Table> TABLE_TEAM_MEMBERS = Id.of("TeamMembers");

    public final static Id<Metadata.Column> COLUMN_TEAM_NAME = Id.of("TeamName");
    public final static Id<Metadata.Column> COLUMN_LOCATION = Id.of("Location");
    public final static Id<Metadata.Column> COLUMN_MEMBER_ID = Id.of("MemberID");
    public final static Id<Metadata.Column> COLUMN_MEMBER_NAME = Id.of("MemberName");
    public final static Id<Metadata.Column> COLUMN_TEAM_MEMBERS_ID = Id.of("TeamMembersID");

    public final static Id<Metadata.PrimaryKey> MY_SQL_PRIMARY_KEY_DEFAULT_NAME = Id.of("PRIMARY");



    private static final QualifiedTableId TEAM_MEMBERS = QualifiedTableId.builder()
            .schemaId(SCHEMA_2)
            .tableId(Id.of("TeamMembers"))
            .build();

    private static final QualifiedTableId TEAMS = QualifiedTableId.builder()
            .schemaId(SCHEMA_1)
            .tableId(Id.of("Teams"))
            .build();

    private static final QualifiedTableId MEMBERS = QualifiedTableId.builder()
            .schemaId(SCHEMA_2)
            .tableId(Id.of("Members"))
            .build();

    private static final QualifiedForeignKeyId TEAM_MEMBERS_2_TEAMS_FOREIGN_KEY = QualifiedForeignKeyId.builder()
            .qualifiedTableId(TEAM_MEMBERS)
            .foreignKeyId(Id.of("FK_TeamMembers_TeamID"))
            .build();

    private static final QualifiedForeignKeyId TEAM_MEMBERS_2_MEMBERS_FOREIGN_KEY = QualifiedForeignKeyId.builder()
            .qualifiedTableId(TEAM_MEMBERS)
            .foreignKeyId(Id.of("FK_TeamMembers_MemberID"))
            .build();

    private static final QualifiedPrimaryKeyId TEAMS_PRIMARY_KEY = QualifiedPrimaryKeyId.builder()
            .qualifiedTableId(TEAMS)
            .primaryKeyId(Id.of("PK_Teams"))
            .build();

    private static final QualifiedPrimaryKeyId MEMBERS_PRIMARY_KEY = QualifiedPrimaryKeyId.builder()
            .qualifiedTableId(MEMBERS)
            .primaryKeyId(Id.of("PK_Members"))
            .build();

    private static final QualifiedPrimaryKeyId TEAM_MEMBERS_PRIMARY_KEY = QualifiedPrimaryKeyId.builder()
            .qualifiedTableId(TEAM_MEMBERS)
            .primaryKeyId(Id.of("PK_TeamMembers"))
            .build();

    @SneakyThrows
    public static void executeTest(SiardArchivesHandler siardArchivesHandler, String jdbcUrl) {
        // given
        val actualArchive = siardArchivesHandler.prepareEmpty();

        // when
        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + jdbcUrl,
                "-u:" + "it_user",
                "-p:" + "it_password",
                "-s:" + actualArchive.getPathToArchiveFile()
        });

        // then
        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());

        actualArchive.preserveArchive();

        val metadataExplorer = actualArchive.exploreMetadata();
        metadataExplorer.tryFindByColumnId(QualifiedColumnId.builder()
                .schemaId(SCHEMA_1)
                .tableId(TABLE_TEAMS)
                .columnId(COLUMN_TEAM_NAME)
                .build());

        Assertions.assertThat(metadataExplorer.tryFindPrimaryKey(TEAMS))
                        .contains(Metadata.PrimaryKey.builder()
                                .name(MY_SQL_PRIMARY_KEY_DEFAULT_NAME) // It seems that mysql has a default name for a PK
                                .columns(CollectionsHelper.setOf(
                                        COLUMN_LOCATION,
                                        COLUMN_TEAM_NAME
                                ))
                                .build());

        Assertions.assertThat(metadataExplorer.tryFindPrimaryKey(MEMBERS))
                .contains(Metadata.PrimaryKey.builder()
                        .name(MY_SQL_PRIMARY_KEY_DEFAULT_NAME) // It seems that mysql has a default name for a PK
                        .columns(CollectionsHelper.setOf(COLUMN_MEMBER_ID))
                        .build());

        Assertions.assertThat(metadataExplorer.tryFindPrimaryKey(TEAM_MEMBERS))
                .contains(Metadata.PrimaryKey.builder()
                        .name(MY_SQL_PRIMARY_KEY_DEFAULT_NAME) // It seems that mysql has a default name for a PK
                        .columns(CollectionsHelper.setOf(COLUMN_TEAM_MEMBERS_ID))
                        .build());

        Assertions.assertThat(metadataExplorer.findForeignKey(TEAM_MEMBERS_2_TEAMS_FOREIGN_KEY))
                .isEqualTo(Metadata.ForeignKey.builder()
                        .name(TEAM_MEMBERS_2_TEAMS_FOREIGN_KEY.getForeignKeyId())
                        .referencedSchema(TEAMS.getSchemaId())
                        .referencedTable(TEAMS.getTableId())
                        .references(CollectionsHelper.setOf(Metadata.Reference.builder()
                                        .column(COLUMN_TEAM_NAME)
                                        .referenced(COLUMN_TEAM_NAME)
                                        .build(),
                                Metadata.Reference.builder()
                                        .column(COLUMN_LOCATION)
                                        .referenced(COLUMN_LOCATION)
                                        .build()))
                        .deleteAction(Optional.of(StringWrapper.of("CASCADE")))
                        .updateAction(Optional.of(StringWrapper.of("CASCADE")))
                        .build());

        Assertions.assertThat(metadataExplorer.findForeignKey(TEAM_MEMBERS_2_MEMBERS_FOREIGN_KEY))
                .isEqualTo(Metadata.ForeignKey.builder()
                        .name(TEAM_MEMBERS_2_MEMBERS_FOREIGN_KEY.getForeignKeyId())
                        .referencedSchema(MEMBERS.getSchemaId())
                        .referencedTable(MEMBERS.getTableId())
                        .references(CollectionsHelper.setOf(Metadata.Reference.builder()
                                        .column(COLUMN_MEMBER_ID)
                                        .referenced(COLUMN_MEMBER_ID)
                                        .build()))
                        .deleteAction(Optional.of(StringWrapper.of("NO ACTION")))
                        .updateAction(Optional.of(StringWrapper.of("NO ACTION")))
                        .build());
    }
}
