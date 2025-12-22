package ch.admin.bar.siard2.cmd.mysql.usecases.types.download.spatialtypes;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedColumnId;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedTableId;
import lombok.SneakyThrows;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Assert;

public class MySqlSpatialDataTypes {

    /**
     * +---------------------------------------------+
     * | SpatialDataTable                            |
     * +-------------------------------------------- +
     * | id INT (PK)                                 |
     * | point_column POINT                          |
     * | linestring_column LINESTRING                |
     * | polygon_column POLYGON                      |
     * | multipoint_column MULTIPOINT                |
     * | multilinestring_column MULTILINESTRING      |
     * | multipolygon_column MULTIPOLYGON            |
     * | geometry_column GEOMETRY                    |
     * | geometrycollection_column GEOMETRYCOLLECTION|
     * +---------------------------------------------+
     */
    public final static String INIT_SCRIPT = "mysql/usecases/types/download/spatialtypes/use-all-spatial-data-types_mysql.sql";

    private static final QualifiedTableId TABLE = QualifiedTableId.builder()
            .schemaId(Id.of("Schema1"))
            .tableId(Id.of("SpatialDataTable"))
            .build();

    public final static QualifiedColumnId COLUMN_ID = TABLE.createQualifiedColumnId(Id.of("id"));
    public final static QualifiedColumnId COLUMN_POINT = TABLE.createQualifiedColumnId(Id.of("point_column"));
    public final static QualifiedColumnId COLUMN_LINESTRING = TABLE.createQualifiedColumnId(Id.of("linestring_column"));
    public final static QualifiedColumnId COLUMN_POLYGON = TABLE.createQualifiedColumnId(Id.of("polygon_column"));
    public final static QualifiedColumnId COLUMN_MULTIPOINT = TABLE.createQualifiedColumnId(Id.of("multipoint_column"));
    public final static QualifiedColumnId COLUMN_MULTILINESTRING = TABLE.createQualifiedColumnId(Id.of("multilinestring_column"));
    public final static QualifiedColumnId COLUMN_MULTIPOLYGON = TABLE.createQualifiedColumnId(Id.of("multipolygon_column"));
    public final static QualifiedColumnId COLUMN_GEOMETRY = TABLE.createQualifiedColumnId(Id.of("geometry_column"));
    public final static QualifiedColumnId COLUMN_GEOMETRYCOLLECTION = TABLE.createQualifiedColumnId(Id.of("geometrycollection_column"));

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

        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_ID)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_POINT)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_LINESTRING)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_POLYGON)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_MULTIPOINT)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_MULTILINESTRING)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_MULTIPOLYGON)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_GEOMETRY)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_GEOMETRYCOLLECTION)).isPresent();

        // TODO: Test content
    }
}
