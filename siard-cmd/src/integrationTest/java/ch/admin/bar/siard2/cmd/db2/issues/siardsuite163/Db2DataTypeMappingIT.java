package ch.admin.bar.siard2.cmd.db2.issues.siardsuite163;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedColumnId;
import lombok.val;
import org.assertj.core.api.SoftAssertions;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.Db2Container;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

public class Db2DataTypeMappingIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public Db2Container db = new Db2Container(DockerImageName.parse("ibmcom/db2:11.5.8.0"))
            .acceptLicense()
            .withInitScript(SqlScripts.Db2.SIARDSUITE_163_DATATYPE_MAPPING);

    @Test
    public void downloadArchive() throws SQLException, IOException, ClassNotFoundException {
        val siardArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb dbToSiard = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + siardArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardFromDb.iRETURN_OK, dbToSiard.getReturn());

        val metadataExplorer = siardArchive.exploreMetadata();
        val softly = new SoftAssertions();

        // CHAR
        val charCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("CHAR_COL"))
                .build());
        softly.assertThat(charCol.getType()).contains(Id.of("CHAR(1)"));
        softly.assertThat(charCol.getTypeOriginal()).contains(Id.of("CHAR"));

        // CHAR(n)
        val charNCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("CHAR_N_COL"))
                .build());
        softly.assertThat(charNCol.getType()).contains(Id.of("CHAR(10)"));
        softly.assertThat(charNCol.getTypeOriginal()).contains(Id.of("CHAR"));

        // VARCHAR(n)
        val varcharNCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("VARCHAR_N_COL"))
                .build());
        softly.assertThat(varcharNCol.getType()).contains(Id.of("VARCHAR(100)"));
        softly.assertThat(varcharNCol.getTypeOriginal()).contains(Id.of("VARCHAR"));

        // CLOB
        val clobCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("CLOB_COL"))
                .build());
        softly.assertThat(clobCol.getType()).contains(Id.of("CLOB(1048576)"));
        softly.assertThat(clobCol.getTypeOriginal()).contains(Id.of("CLOB"));

        // XML
        val xmlCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("XML_COL"))
                .build());
        softly.assertThat(xmlCol.getType()).contains(Id.of("XML"));
        softly.assertThat(xmlCol.getTypeOriginal()).contains(Id.of("XML"));

        // GRAPHIC
        val graphicCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("GRAPHIC_COL"))
                .build());
        softly.assertThat(graphicCol.getType()).contains(Id.of("NCHAR(1)"));
        softly.assertThat(graphicCol.getTypeOriginal()).contains(Id.of("GRAPHIC"));

        // GRAPHIC(n)
        val graphicNCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("GRAPHIC_N_COL"))
                .build());
        softly.assertThat(graphicNCol.getType()).contains(Id.of("NCHAR(10)"));
        softly.assertThat(graphicNCol.getTypeOriginal()).contains(Id.of("GRAPHIC"));

        // VARGRAPHIC(n)
        val vargraphicNCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("VARGRAPHIC_N_COL"))
                .build());
        softly.assertThat(vargraphicNCol.getType()).contains(Id.of("NCHAR VARYING(100)"));
        softly.assertThat(vargraphicNCol.getTypeOriginal()).contains(Id.of("VARGRAPHIC"));

        // DBCLOB
        val dbclobCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("DBCLOB_COL"))
                .build());
        softly.assertThat(dbclobCol.getType()).contains(Id.of("NCLOB(1048576)"));
        softly.assertThat(dbclobCol.getTypeOriginal()).contains(Id.of("DBCLOB"));

        // CHAR FOR BIT DATA
        val charBitCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("CHAR_BIT_COL"))
                .build());
        softly.assertThat(charBitCol.getType()).contains(Id.of("BINARY(1)"));
        softly.assertThat(charBitCol.getTypeOriginal()).contains(Id.of("CHAR () FOR BIT DATA"));

        // CHAR(n) FOR BIT DATA
        val charNBitCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("CHAR_N_BIT_COL"))
                .build());
        softly.assertThat(charNBitCol.getType()).contains(Id.of("BINARY(16)"));
        softly.assertThat(charNBitCol.getTypeOriginal()).contains(Id.of("CHAR () FOR BIT DATA"));

        // VARCHAR(n) FOR BIT DATA
        val varcharNBitCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("VARCHAR_N_BIT_COL"))
                .build());
        softly.assertThat(varcharNBitCol.getType()).contains(Id.of("VARBINARY(100)"));
        softly.assertThat(varcharNBitCol.getTypeOriginal()).contains(Id.of("VARCHAR () FOR BIT DATA"));

        // BLOB
        val blobCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("BLOB_COL"))
                .build());
        softly.assertThat(blobCol.getType()).contains(Id.of("BLOB(1048576)"));
        softly.assertThat(blobCol.getTypeOriginal()).contains(Id.of("BLOB"));

        // SMALLINT
        val smallintCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("SMALLINT_COL"))
                .build());
        softly.assertThat(smallintCol.getType()).contains(Id.of("SMALLINT"));
        softly.assertThat(smallintCol.getTypeOriginal()).contains(Id.of("SMALLINT"));

        // INTEGER
        val integerCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("INTEGER_COL"))
                .build());
        softly.assertThat(integerCol.getType()).contains(Id.of("INT"));
        softly.assertThat(integerCol.getTypeOriginal()).contains(Id.of("INTEGER"));

        // BIGINT
        val bigintCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("BIGINT_COL"))
                .build());
        softly.assertThat(bigintCol.getType()).contains(Id.of("BIGINT"));
        softly.assertThat(bigintCol.getTypeOriginal()).contains(Id.of("BIGINT"));

        // NUMERIC
        val numericCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("NUMERIC_COL"))
                .build());
        softly.assertThat(numericCol.getType()).contains(Id.of("DEC(5)"));
        softly.assertThat(numericCol.getTypeOriginal()).contains(Id.of("DECIMAL"));

        // NUMERIC(p)
        val numericPCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("NUMERIC_P_COL"))
                .build());
        softly.assertThat(numericPCol.getType()).contains(Id.of("DEC(10)"));
        softly.assertThat(numericPCol.getTypeOriginal()).contains(Id.of("DECIMAL"));

        // NUMERIC(p,s)
        val numericPSCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("NUMERIC_PS_COL"))
                .build());
        softly.assertThat(numericPSCol.getType()).contains(Id.of("DEC(10, 2)"));
        softly.assertThat(numericPSCol.getTypeOriginal()).contains(Id.of("DECIMAL"));

        // DECIMAL
        val decimalCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("DECIMAL_COL"))
                .build());
        softly.assertThat(decimalCol.getType()).contains(Id.of("DEC(5)"));
        softly.assertThat(decimalCol.getTypeOriginal()).contains(Id.of("DECIMAL"));

        // DECIMAL(p)
        val decimalPCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("DECIMAL_P_COL"))
                .build());
        softly.assertThat(decimalPCol.getType()).contains(Id.of("DEC(10)"));
        softly.assertThat(decimalPCol.getTypeOriginal()).contains(Id.of("DECIMAL"));

        // DECIMAL(p,s)
        val decimalPSCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("DECIMAL_PS_COL"))
                .build());
        softly.assertThat(decimalPSCol.getType()).contains(Id.of("DEC(10, 2)"));
        softly.assertThat(decimalPSCol.getTypeOriginal()).contains(Id.of("DECIMAL"));

        // FLOAT
        val floatCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("FLOAT_COL"))
                .build());
        softly.assertThat(floatCol.getType()).contains(Id.of("DOUBLE PRECISION"));
        softly.assertThat(floatCol.getTypeOriginal()).contains(Id.of("DOUBLE"));

        // FLOAT(p) where p <= 7
        val floatPSmallCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("FLOAT_P_SMALL_COL"))
                .build());
        softly.assertThat(floatPSmallCol.getType()).contains(Id.of("REAL"));
        softly.assertThat(floatPSmallCol.getTypeOriginal()).contains(Id.of("REAL"));

        // FLOAT(p) where p > 7
        val floatPLargeCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("FLOAT_P_LARGE_COL"))
                .build());
        softly.assertThat(floatPLargeCol.getType()).contains(Id.of("REAL"));
        softly.assertThat(floatPLargeCol.getTypeOriginal()).contains(Id.of("REAL"));

        // REAL
        val realCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("REAL_COL"))
                .build());
        softly.assertThat(realCol.getType()).contains(Id.of("REAL"));
        softly.assertThat(realCol.getTypeOriginal()).contains(Id.of("REAL"));

        // DOUBLE
        val doubleCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("DOUBLE_COL"))
                .build());
        softly.assertThat(doubleCol.getType()).contains(Id.of("DOUBLE PRECISION"));
        softly.assertThat(doubleCol.getTypeOriginal()).contains(Id.of("DOUBLE"));

        // DATE
        val dateCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("DATE_COL"))
                .build());
        softly.assertThat(dateCol.getType()).contains(Id.of("DATE"));
        softly.assertThat(dateCol.getTypeOriginal()).contains(Id.of("DATE"));

        // TIME
        val timeCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("TIME_COL"))
                .build());
        softly.assertThat(timeCol.getType()).contains(Id.of("TIME"));
        softly.assertThat(timeCol.getTypeOriginal()).contains(Id.of("TIME"));

        // TIMESTAMP
        val timestampCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("TIMESTAMP_COL"))
                .build());
        softly.assertThat(timestampCol.getType()).contains(Id.of("TIMESTAMP"));
        softly.assertThat(timestampCol.getTypeOriginal()).contains(Id.of("TIMESTAMP"));

        // TIMESTAMP(n)
        val timestampNCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DB2INST1"))
                .tableId(Id.of("DATATYPE_MAPPING_TEST"))
                .columnId(Id.of("TIMESTAMP_N_COL"))
                .build());
        softly.assertThat(timestampNCol.getType()).contains(Id.of("TIMESTAMP(9)"));
        softly.assertThat(timestampNCol.getTypeOriginal()).contains(Id.of("TIMESTAMP"));

        softly.assertAll();
    }
}
