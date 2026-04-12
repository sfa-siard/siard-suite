package ch.admin.bar.siard2.cmd.mssql.issues.siardsuite163;

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
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

public class MsSqlDataTypeMappingIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MSSQLServerContainer<?> db = new MSSQLServerContainer<>(DockerImageName.parse("mcr.microsoft.com/mssql/server:2017-CU12"))
            .acceptLicense()
            .withInitScript(SqlScripts.MsSQL.SIARDSUITE_163_DATATYPE_MAPPING);

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
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("char_col"))
                .build());
        softly.assertThat(charCol.getType()).contains(Id.of("CHARACTER(1)"));
        softly.assertThat(charCol.getTypeOriginal()).contains(Id.of("char"));

        // CHAR(n)
        val charNCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("char_n_col"))
                .build());
        softly.assertThat(charNCol.getType()).contains(Id.of("CHARACTER(10)"));
        softly.assertThat(charNCol.getTypeOriginal()).contains(Id.of("char(10)"));

        // VARCHAR
        val varcharCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("varchar_col"))
                .build());
        softly.assertThat(varcharCol.getType()).contains(Id.of("CHARACTER VARYING(1)"));
        softly.assertThat(varcharCol.getTypeOriginal()).contains(Id.of("varchar"));

        // VARCHAR(n)
        val varcharNCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("varchar_n_col"))
                .build());
        softly.assertThat(varcharNCol.getType()).contains(Id.of("CHARACTER VARYING(100)"));
        softly.assertThat(varcharNCol.getTypeOriginal()).contains(Id.of("varchar(100)"));

        // TEXT
        val textCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("text_col"))
                .build());
        softly.assertThat(textCol.getType()).contains(Id.of("CHARACTER LARGE OBJECT"));
        softly.assertThat(textCol.getTypeOriginal()).contains(Id.of("text"));

        // NCHAR
        val ncharCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("nchar_col"))
                .build());
        softly.assertThat(ncharCol.getType()).contains(Id.of("NATIONAL CHARACTER(1)"));
        softly.assertThat(ncharCol.getTypeOriginal()).contains(Id.of("nchar"));

        // NCHAR(n)
        val ncharNCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("nchar_n_col"))
                .build());
        softly.assertThat(ncharNCol.getType()).contains(Id.of("NATIONAL CHARACTER(10)"));
        softly.assertThat(ncharNCol.getTypeOriginal()).contains(Id.of("nchar(10)"));

        // NVARCHAR
        val nvarcharCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("nvarchar_col"))
                .build());
        softly.assertThat(nvarcharCol.getType()).contains(Id.of("NATIONAL CHARACTER VARYING(1)"));
        softly.assertThat(nvarcharCol.getTypeOriginal()).contains(Id.of("nvarchar"));

        // NVARCHAR(n)
        val nvarcharNCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("nvarchar_n_col"))
                .build());
        softly.assertThat(nvarcharNCol.getType()).contains(Id.of("NATIONAL CHARACTER VARYING(100)"));
        softly.assertThat(nvarcharNCol.getTypeOriginal()).contains(Id.of("nvarchar(100)"));

        // NTEXT
        val ntextCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("ntext_col"))
                .build());
        softly.assertThat(ntextCol.getType()).contains(Id.of("NATIONAL CHARACTER LARGE OBJECT"));
        softly.assertThat(ntextCol.getTypeOriginal()).contains(Id.of("ntext"));

        // XML
        val xmlCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("xml_col"))
                .build());
        softly.assertThat(xmlCol.getType()).contains(Id.of("XML"));
        softly.assertThat(xmlCol.getTypeOriginal()).contains(Id.of("xml"));

        // TINYINT
        val tinyintCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("tinyint_col"))
                .build());
        softly.assertThat(tinyintCol.getType()).contains(Id.of("SMALLINT"));
        softly.assertThat(tinyintCol.getTypeOriginal()).contains(Id.of("tinyint"));

        // SMALLINT
        val smallintCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("smallint_col"))
                .build());
        softly.assertThat(smallintCol.getType()).contains(Id.of("SMALLINT"));
        softly.assertThat(smallintCol.getTypeOriginal()).contains(Id.of("smallint"));

        // INT
        val intCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("int_col"))
                .build());
        softly.assertThat(intCol.getType()).contains(Id.of("INTEGER"));
        softly.assertThat(intCol.getTypeOriginal()).contains(Id.of("int"));

        // BIGINT
        val bigintCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("bigint_col"))
                .build());
        softly.assertThat(bigintCol.getType()).contains(Id.of("NUMERIC(19)"));
        softly.assertThat(bigintCol.getTypeOriginal()).contains(Id.of("bigint"));

        // NUMERIC
        val numericCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("numeric_col"))
                .build());
        softly.assertThat(numericCol.getType()).contains(Id.of("NUMERIC(18)"));
        softly.assertThat(numericCol.getTypeOriginal()).contains(Id.of("numeric"));

        // NUMERIC(p)
        val numericPCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("numeric_p_col"))
                .build());
        softly.assertThat(numericPCol.getType()).contains(Id.of("NUMERIC(10)"));
        softly.assertThat(numericPCol.getTypeOriginal()).contains(Id.of("numeric(10)"));

        // NUMERIC(p,s)
        val numericPSCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("numeric_ps_col"))
                .build());
        softly.assertThat(numericPSCol.getType()).contains(Id.of("NUMERIC(10,2)"));
        softly.assertThat(numericPSCol.getTypeOriginal()).contains(Id.of("numeric(10,2)"));

        // DECIMAL
        val decimalCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("decimal_col"))
                .build());
        softly.assertThat(decimalCol.getType()).contains(Id.of("DECIMAL(18)"));
        softly.assertThat(decimalCol.getTypeOriginal()).contains(Id.of("decimal"));

        // DECIMAL(p)
        val decimalPCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("decimal_p_col"))
                .build());
        softly.assertThat(decimalPCol.getType()).contains(Id.of("DECIMAL(10)"));
        softly.assertThat(decimalPCol.getTypeOriginal()).contains(Id.of("decimal(10)"));

        // DECIMAL(p,s)
        val decimalPSCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("decimal_ps_col"))
                .build());
        softly.assertThat(decimalPSCol.getType()).contains(Id.of("DECIMAL(10,2)"));
        softly.assertThat(decimalPSCol.getTypeOriginal()).contains(Id.of("decimal(10,2)"));

        // SMALLMONEY
        val smallmoneyCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("smallmoney_col"))
                .build());
        softly.assertThat(smallmoneyCol.getType()).contains(Id.of("DECIMAL(10,4)"));
        softly.assertThat(smallmoneyCol.getTypeOriginal()).contains(Id.of("smallmoney"));

        // MONEY
        val moneyCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("money_col"))
                .build());
        softly.assertThat(moneyCol.getType()).contains(Id.of("DECIMAL(19,4)"));
        softly.assertThat(moneyCol.getTypeOriginal()).contains(Id.of("money"));

        // FLOAT
        val floatCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("float_col"))
                .build());
        softly.assertThat(floatCol.getType()).contains(Id.of("DOUBLE PRECISION"));
        softly.assertThat(floatCol.getTypeOriginal()).contains(Id.of("float"));

        // FLOAT(p) where p <= 7
        val floatPSmallCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("float_p_small_col"))
                .build());
        softly.assertThat(floatPSmallCol.getType()).contains(Id.of("REAL"));
        softly.assertThat(floatPSmallCol.getTypeOriginal()).contains(Id.of("float"));

        // FLOAT(p) where p > 7
        val floatPLargeCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("float_p_large_col"))
                .build());
        softly.assertThat(floatPLargeCol.getType()).contains(Id.of("DOUBLE PRECISION"));
        softly.assertThat(floatPLargeCol.getTypeOriginal()).contains(Id.of("float"));

        // REAL
        val realCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("real_col"))
                .build());
        softly.assertThat(realCol.getType()).contains(Id.of("REAL"));
        softly.assertThat(realCol.getTypeOriginal()).contains(Id.of("real"));

        // BIT
        val bitCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("bit_col"))
                .build());
        softly.assertThat(bitCol.getType()).contains(Id.of("BOOLEAN"));
        softly.assertThat(bitCol.getTypeOriginal()).contains(Id.of("bit"));

        // BINARY
        val binaryCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("binary_col"))
                .build());
        softly.assertThat(binaryCol.getType()).contains(Id.of("BIT(8)"));
        softly.assertThat(binaryCol.getTypeOriginal()).contains(Id.of("binary"));

        // BINARY(n)
        val binaryNCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("binary_n_col"))
                .build());
        softly.assertThat(binaryNCol.getType()).contains(Id.of("BIT(128)"));
        softly.assertThat(binaryNCol.getTypeOriginal()).contains(Id.of("binary(16)"));

        // VARBINARY
        val varbinaryCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("varbinary_col"))
                .build());
        softly.assertThat(varbinaryCol.getType()).contains(Id.of("BIT VARYING(8)"));
        softly.assertThat(varbinaryCol.getTypeOriginal()).contains(Id.of("varbinary"));

        // VARBINARY(n)
        val varbinaryNCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("varbinary_n_col"))
                .build());
        softly.assertThat(varbinaryNCol.getType()).contains(Id.of("BIT VARYING(800)"));
        softly.assertThat(varbinaryNCol.getTypeOriginal()).contains(Id.of("varbinary(100)"));

        // IMAGE
        val imageCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("image_col"))
                .build());
        softly.assertThat(imageCol.getType()).contains(Id.of("BINARY LARGE OBJECT"));
        softly.assertThat(imageCol.getTypeOriginal()).contains(Id.of("image"));

        // DATE
        val dateCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("date_col"))
                .build());
        softly.assertThat(dateCol.getType()).contains(Id.of("DATE"));
        softly.assertThat(dateCol.getTypeOriginal()).contains(Id.of("date"));

        // TIME
        val timeCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("time_col"))
                .build());
        softly.assertThat(timeCol.getType()).contains(Id.of("TIME(7)"));
        softly.assertThat(timeCol.getTypeOriginal()).contains(Id.of("time"));

        // DATETIME
        val datetimeCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("datetime_col"))
                .build());
        softly.assertThat(datetimeCol.getType()).contains(Id.of("TIMESTAMP(7)"));
        softly.assertThat(datetimeCol.getTypeOriginal()).contains(Id.of("datetime"));

        // DATETIME2
        val datetime2Col = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("datetime2_col"))
                .build());
        softly.assertThat(datetime2Col.getType()).contains(Id.of("TIMESTAMP(7)"));
        softly.assertThat(datetime2Col.getTypeOriginal()).contains(Id.of("datetime2"));

        // SMALLDATETIME
        val smalldatetimeCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("DataTypeMappingTest"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("smalldatetime_col"))
                .build());
        softly.assertThat(smalldatetimeCol.getType()).contains(Id.of("TIMESTAMP"));
        softly.assertThat(smalldatetimeCol.getTypeOriginal()).contains(Id.of("smalldatetime"));

        softly.assertAll();
    }
}
