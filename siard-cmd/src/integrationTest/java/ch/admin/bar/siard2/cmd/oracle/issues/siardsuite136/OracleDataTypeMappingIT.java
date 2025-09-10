package ch.admin.bar.siard2.cmd.oracle.issues.siardsuite136;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.utils.ConsoleLogConsumer;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.TestResourcesResolver;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedColumnId;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.sql.SQLException;

public class OracleDataTypeMappingIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public OracleContainer db = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
            .withLogConsumer(new ConsoleLogConsumer())
            .withCopyFileToContainer(
                    MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.Oracle.CREATE_USER_WITH_ALL_PRIVILEGES).toPath()),
                    "/container-entrypoint-initdb.d/00_create_user.sql")
            .withCopyFileToContainer(
                    MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.Oracle.SIARDSUITE_136_DATATYPE_MAPPING).toPath()),
                    "/container-entrypoint-initdb.d/datatype-mapping.sql");

    @Test
    public void downloadArchive() throws SQLException, IOException, ClassNotFoundException {
        val siardArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb dbToSiard = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + "IT_USER",
                "-p:" + "password",
                "-s:" + siardArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardFromDb.iRETURN_OK, dbToSiard.getReturn());

        val metadataExplorer = siardArchive.exploreMetadata();

        val id = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("ID"))
                .build());
        Assertions.assertThat(id.getType()).contains(Id.of("FLOAT(38)"));
        Assertions.assertThat(id.getTypeOriginal()).contains(Id.of("NUMBER"));

        val intCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("INT_COL"))
                .build());
        Assertions.assertThat(intCol.getType()).contains(Id.of("BIGINT"));
        Assertions.assertThat(intCol.getTypeOriginal()).contains(Id.of("NUMBER"));

        val numCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("NUM_COL"))
                .build());
        Assertions.assertThat(numCol.getType()).contains(Id.of("FLOAT(38)"));
        Assertions.assertThat(numCol.getTypeOriginal()).contains(Id.of("NUMBER"));

        val numPrecCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("NUM_PREC_COL"))
                .build());
        Assertions.assertThat(numPrecCol.getType()).contains(Id.of("INT"));
        Assertions.assertThat(numPrecCol.getTypeOriginal()).contains(Id.of("NUMBER(10,0)"));

        val numPrecScaleCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("NUM_PREC_SCALE_COL"))
                .build());
        Assertions.assertThat(numPrecScaleCol.getType()).contains(Id.of("DEC(10, 2)"));
        Assertions.assertThat(numPrecScaleCol.getTypeOriginal()).contains(Id.of("NUMBER(10,2)"));

        val smallintCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("SMALLINT_COL"))
                .build());
        Assertions.assertThat(smallintCol.getType()).contains(Id.of("BIGINT"));
        Assertions.assertThat(smallintCol.getTypeOriginal()).contains(Id.of("NUMBER"));

        val decimalCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("DECIMAL_COL"))
                .build());
        Assertions.assertThat(decimalCol.getType()).contains(Id.of("BIGINT"));
        Assertions.assertThat(decimalCol.getTypeOriginal()).contains(Id.of("NUMBER"));

        val decimalPrecScaleCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("DECIMAL_PREC_SCALE_COL"))
                .build());
        Assertions.assertThat(decimalPrecScaleCol.getType()).contains(Id.of("DEC(10, 2)"));
        Assertions.assertThat(decimalPrecScaleCol.getTypeOriginal()).contains(Id.of("NUMBER(10,2)"));

        val numericCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("NUMERIC_COL"))
                .build());
        Assertions.assertThat(numericCol.getType()).contains(Id.of("BIGINT"));
        Assertions.assertThat(numericCol.getTypeOriginal()).contains(Id.of("NUMBER"));

        val numericPrecScaleCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("NUMERIC_PREC_SCALE_COL"))
                .build());
        Assertions.assertThat(numericPrecScaleCol.getType()).contains(Id.of("DEC(10, 2)"));
        Assertions.assertThat(numericPrecScaleCol.getTypeOriginal()).contains(Id.of("NUMBER(10,2)"));

        val floatCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("FLOAT_COL"))
                .build());
        Assertions.assertThat(floatCol.getType()).contains(Id.of("FLOAT(10)"));
        Assertions.assertThat(floatCol.getTypeOriginal()).contains(Id.of("FLOAT(10)"));

        val realCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("REAL_COL"))
                .build());
        Assertions.assertThat(realCol.getType()).contains(Id.of("FLOAT(63)"));
        Assertions.assertThat(realCol.getTypeOriginal()).contains(Id.of("FLOAT(63)"));

        val doublePrecCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("DOUBLE_PREC_COL"))
                .build());
        Assertions.assertThat(doublePrecCol.getType()).contains(Id.of("FLOAT(126)"));
        Assertions.assertThat(doublePrecCol.getTypeOriginal()).contains(Id.of("FLOAT(126)"));

        val binaryFloatCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("BINARY_FLOAT_COL"))
                .build());
        Assertions.assertThat(binaryFloatCol.getType()).contains(Id.of("REAL"));
        Assertions.assertThat(binaryFloatCol.getTypeOriginal()).contains(Id.of("BINARY_FLOAT"));

        val binaryDoubleCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("BINARY_DOUBLE_COL"))
                .build());
        Assertions.assertThat(binaryDoubleCol.getType()).contains(Id.of("DOUBLE PRECISION"));
        Assertions.assertThat(binaryDoubleCol.getTypeOriginal()).contains(Id.of("BINARY_DOUBLE"));

        val charCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("CHAR_COL"))
                .build());
        Assertions.assertThat(charCol.getType()).contains(Id.of("CHAR(1)"));
        Assertions.assertThat(charCol.getTypeOriginal()).contains(Id.of("CHAR(1)"));

        val charNCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("CHAR_N_COL"))
                .build());
        Assertions.assertThat(charNCol.getType()).contains(Id.of("CHAR(10)"));
        Assertions.assertThat(charNCol.getTypeOriginal()).contains(Id.of("CHAR(10)"));

        val varcharCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("VARCHAR_COL"))
                .build());
        Assertions.assertThat(varcharCol.getType()).contains(Id.of("VARCHAR(100)"));
        Assertions.assertThat(varcharCol.getTypeOriginal()).contains(Id.of("VARCHAR2(100)"));

        val varchar2Col = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("VARCHAR2_COL"))
                .build());
        Assertions.assertThat(varchar2Col.getType()).contains(Id.of("VARCHAR(100)"));
        Assertions.assertThat(varchar2Col.getTypeOriginal()).contains(Id.of("VARCHAR2(100)"));

        val nCharCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("NCHAR_COL"))
                .build());
        //TODO is this a bug, 2 instead of 1?
        Assertions.assertThat(nCharCol.getType()).contains(Id.of("NCHAR(2)"));
        Assertions.assertThat(nCharCol.getTypeOriginal()).contains(Id.of("NCHAR(2)"));

        val nCharNCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("NCHAR_N_COL"))
                .build());
        //TODO is this a bug, 20 instead of 10?
        Assertions.assertThat(nCharNCol.getType()).contains(Id.of("NCHAR(20)"));
        Assertions.assertThat(nCharNCol.getTypeOriginal()).contains(Id.of("NCHAR(20)"));

        val nVarchar2Col = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("NVARCHAR2_COL"))
                .build());
        //TODO is this a bug, 200 instead of 100?
        Assertions.assertThat(nVarchar2Col.getType()).contains(Id.of("NCHAR VARYING(200)"));
        Assertions.assertThat(nVarchar2Col.getTypeOriginal()).contains(Id.of("NVARCHAR2(200)"));

        val clobCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("CLOB_COL"))
                .build());
        Assertions.assertThat(clobCol.getType()).contains(Id.of("CLOB"));
        Assertions.assertThat(clobCol.getTypeOriginal()).contains(Id.of("CLOB"));

        val nClobCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("NCLOB_COL"))
                .build());
        Assertions.assertThat(nClobCol.getType()).contains(Id.of("NCLOB"));
        Assertions.assertThat(nClobCol.getTypeOriginal()).contains(Id.of("NCLOB"));

        val blobCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("BLOB_COL"))
                .build());
        Assertions.assertThat(blobCol.getType()).contains(Id.of("BLOB"));
        Assertions.assertThat(blobCol.getTypeOriginal()).contains(Id.of("BLOB"));

        val rawCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("RAW_COL"))
                .build());
        Assertions.assertThat(rawCol.getType()).contains(Id.of("VARBINARY(100)"));
        Assertions.assertThat(rawCol.getTypeOriginal()).contains(Id.of("RAW(100)"));

        val longRawCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("LONGRAW_COL"))
                .build());
        Assertions.assertThat(longRawCol.getType()).contains(Id.of("BLOB"));
        Assertions.assertThat(longRawCol.getTypeOriginal()).contains(Id.of("LONG RAW"));

        val dateCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("DATE_COL"))
                .build());
        Assertions.assertThat(dateCol.getType()).contains(Id.of("TIMESTAMP"));
        Assertions.assertThat(dateCol.getTypeOriginal()).contains(Id.of("DATE"));

        val timestampCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("TIMESTAMP_COL"))
                .build());
        Assertions.assertThat(timestampCol.getType()).contains(Id.of("TIMESTAMP"));
        Assertions.assertThat(timestampCol.getTypeOriginal()).contains(Id.of("TIMESTAMP(6)"));

        val timestampNCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("TIMESTAMP_N_COL"))
                .build());
        Assertions.assertThat(timestampNCol.getType()).contains(Id.of("TIMESTAMP"));
        Assertions.assertThat(timestampNCol.getTypeOriginal()).contains(Id.of("TIMESTAMP(6)"));

        val timestampTzCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("TIMESTAMP_TZ_COL"))
                .build());
        Assertions.assertThat(timestampTzCol.getType()).contains(Id.of("TIMESTAMP"));
        Assertions.assertThat(timestampTzCol.getTypeOriginal()).contains(Id.of("TIMESTAMP(6) WITH TIME ZONE"));

        val timestampLtzCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("TIMESTAMP_LTZ_COL"))
                .build());
        Assertions.assertThat(timestampLtzCol.getType()).contains(Id.of("TIMESTAMP"));
        Assertions.assertThat(timestampLtzCol.getTypeOriginal()).contains(Id.of("TIMESTAMP(6) WITH LOCAL TIME ZONE"));
    }
}
