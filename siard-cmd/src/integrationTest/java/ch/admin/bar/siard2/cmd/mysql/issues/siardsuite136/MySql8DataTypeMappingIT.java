package ch.admin.bar.siard2.cmd.mysql.issues.siardsuite136;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SupportedDbVersions;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedColumnId;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

public class MySql8DataTypeMappingIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MySQLContainer<?> downloadDbMySql8 = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_8_4))
            .withUsername("root")
            .withPassword("public")
            .withInitScript(SqlScripts.MySQL.SIARDSUITE_136_DATATYPE_MAPPING)
            .withConfigurationOverride("mysql/config/mysql-version-support");

    @Test
    public void downloadArchiveMySql8() throws SQLException, IOException, ClassNotFoundException {
        val siardArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb dbToSiard = new SiardFromDb(new String[]{
                "-o",
                "-j:" + downloadDbMySql8.getJdbcUrl(),
                "-u:" + "it_user",
                "-p:" + "it_password",
                "-s:" + siardArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardFromDb.iRETURN_OK, dbToSiard.getReturn());

        val metadataExplorer = siardArchive.exploreMetadata();

        // CHAR
        val charCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("char_col"))
                .build());
        Assertions.assertThat(charCol.getType()).contains(Id.of("CHAR(1)"));
        Assertions.assertThat(charCol.getTypeOriginal()).contains(Id.of("char(1)"));

        // CHAR(n)
        val charNCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("char_n_col"))
                .build());
        Assertions.assertThat(charNCol.getType()).contains(Id.of("CHAR(10)"));
        Assertions.assertThat(charNCol.getTypeOriginal()).contains(Id.of("char(10)"));

        // VARCHAR(n)
        val varcharNCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("varchar_col"))
                .build());
        Assertions.assertThat(varcharNCol.getType()).contains(Id.of("VARCHAR(100)"));
        Assertions.assertThat(varcharNCol.getTypeOriginal()).contains(Id.of("varchar(100)"));

        // TINYTEXT
        val tinytextCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("tinytext_col"))
                .build());
        Assertions.assertThat(tinytextCol.getType()).contains(Id.of("VARCHAR(255)"));
        Assertions.assertThat(tinytextCol.getTypeOriginal()).contains(Id.of("tinytext"));

        // TEXT
        val textCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("text_col"))
                .build());
        Assertions.assertThat(textCol.getType()).contains(Id.of("CLOB(65535)"));
        Assertions.assertThat(textCol.getTypeOriginal()).contains(Id.of("text"));

        // MEDIUMTEXT
        val mediumtextCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("mediumtext_col"))
                .build());
        Assertions.assertThat(mediumtextCol.getType()).contains(Id.of("CLOB(16777215)"));
        Assertions.assertThat(mediumtextCol.getTypeOriginal()).contains(Id.of("mediumtext"));

        // TINYINT
        val tinyintCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("tinyint_col"))
                .build());
        Assertions.assertThat(tinyintCol.getType()).contains(Id.of("SMALLINT"));
        Assertions.assertThat(tinyintCol.getTypeOriginal()).contains(Id.of("tinyint"));

        // SMALLINT
        val smallintCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("smallint_col"))
                .build());
        Assertions.assertThat(smallintCol.getType()).contains(Id.of("SMALLINT"));
        Assertions.assertThat(smallintCol.getTypeOriginal()).contains(Id.of("smallint"));

        // MEDIUMINT
        val mediumintCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("mediumint_col"))
                .build());
        Assertions.assertThat(mediumintCol.getType()).contains(Id.of("INT"));
        Assertions.assertThat(mediumintCol.getTypeOriginal()).contains(Id.of("mediumint"));

        // INT
        val intCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("int_col"))
                .build());
        Assertions.assertThat(intCol.getType()).contains(Id.of("INT"));
        Assertions.assertThat(intCol.getTypeOriginal()).contains(Id.of("int"));

        // BIGINT
        val bigintCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("bigint_col"))
                .build());
        Assertions.assertThat(bigintCol.getType()).contains(Id.of("BIGINT"));
        Assertions.assertThat(bigintCol.getTypeOriginal()).contains(Id.of("bigint"));

        // DECIMAL
        val decimalCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("decimal_col"))
                .build());
        Assertions.assertThat(decimalCol.getType()).contains(Id.of("DEC(10)"));
        Assertions.assertThat(decimalCol.getTypeOriginal()).contains(Id.of("decimal(10,0)"));

        // DECIMAL(n)
        val decimalNCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("decimal_n_col"))
                .build());
        Assertions.assertThat(decimalNCol.getType()).contains(Id.of("DEC(10)"));
        Assertions.assertThat(decimalNCol.getTypeOriginal()).contains(Id.of("decimal(10,0)"));

        // DECIMAL(p,q)
        val decimalPQCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("decimal_pq_col"))
                .build());
        Assertions.assertThat(decimalPQCol.getType()).contains(Id.of("DEC(10, 2)"));
        Assertions.assertThat(decimalPQCol.getTypeOriginal()).contains(Id.of("decimal(10,2)"));

        // NUMERIC
        val numericCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("numeric_col"))
                .build());
        Assertions.assertThat(numericCol.getType()).contains(Id.of("DEC(10)"));
        Assertions.assertThat(numericCol.getTypeOriginal()).contains(Id.of("decimal(10,0)"));

        // NUMERIC(n)
        val numericNCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("numeric_n_col"))
                .build());
        Assertions.assertThat(numericNCol.getType()).contains(Id.of("DEC(10)"));
        Assertions.assertThat(numericNCol.getTypeOriginal()).contains(Id.of("decimal(10,0)"));

        // NUMERIC(p,q)
        val numericPQCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("numeric_pq_col"))
                .build());
        Assertions.assertThat(numericPQCol.getType()).contains(Id.of("DEC(10, 2)"));
        Assertions.assertThat(numericPQCol.getTypeOriginal()).contains(Id.of("decimal(10,2)"));

        // FLOAT
        val floatCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("float_col"))
                .build());
        Assertions.assertThat(floatCol.getType()).contains(Id.of("FLOAT(12)"));
        Assertions.assertThat(floatCol.getTypeOriginal()).contains(Id.of("float"));

        // FLOAT(p)
        val floatPCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("float_p_col"))
                .build());
        Assertions.assertThat(floatPCol.getType()).contains(Id.of("FLOAT(12)"));
        Assertions.assertThat(floatPCol.getTypeOriginal()).contains(Id.of("float"));

        // FLOAT(p,q)
        val floatPQCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("float_pq_col"))
                .build());
        Assertions.assertThat(floatPQCol.getType()).contains(Id.of("FLOAT"));
        Assertions.assertThat(floatPQCol.getTypeOriginal()).contains(Id.of("float(10,2)"));

        // DOUBLE
        val doubleCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("double_col"))
                .build());
        Assertions.assertThat(doubleCol.getType()).contains(Id.of("DOUBLE PRECISION"));
        Assertions.assertThat(doubleCol.getTypeOriginal()).contains(Id.of("double"));

        // DOUBLE(p,q)
        val doublePQCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("double_pq_col"))
                .build());
        Assertions.assertThat(doublePQCol.getType()).contains(Id.of("DOUBLE PRECISION"));
        Assertions.assertThat(doublePQCol.getTypeOriginal()).contains(Id.of("double(15,5)"));

        // BIT
        val bitCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("bit_col"))
                .build());
        Assertions.assertThat(bitCol.getType()).contains(Id.of("BOOLEAN"));
        Assertions.assertThat(bitCol.getTypeOriginal()).contains(Id.of("bit(1)"));

        // BIT(n)
        val bitNCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("bit_n_col"))
                .build());
        Assertions.assertThat(bitNCol.getType()).contains(Id.of("BINARY(8)"));
        Assertions.assertThat(bitNCol.getTypeOriginal()).contains(Id.of("bit(8)"));

        // BINARY(n)
        val binaryNCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("binary_col"))
                .build());
        Assertions.assertThat(binaryNCol.getType()).contains(Id.of("BINARY(16)"));
        Assertions.assertThat(binaryNCol.getTypeOriginal()).contains(Id.of("binary(16)"));

        // VARBINARY(n)
        val varbinaryNCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("varbinary_col"))
                .build());
        Assertions.assertThat(varbinaryNCol.getType()).contains(Id.of("VARBINARY(100)"));
        Assertions.assertThat(varbinaryNCol.getTypeOriginal()).contains(Id.of("varbinary(100)"));

        // TINYBLOB
        val tinyblobCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("tinyblob_col"))
                .build());
        Assertions.assertThat(tinyblobCol.getType()).contains(Id.of("VARBINARY(255)"));
        Assertions.assertThat(tinyblobCol.getTypeOriginal()).contains(Id.of("tinyblob"));

        // BLOB
        val blobCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("blob_col"))
                .build());
        Assertions.assertThat(blobCol.getType()).contains(Id.of("BLOB(65535)"));
        Assertions.assertThat(blobCol.getTypeOriginal()).contains(Id.of("blob"));

        // MEDIUMBLOB
        val mediumblobCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("mediumblob_col"))
                .build());
        Assertions.assertThat(mediumblobCol.getType()).contains(Id.of("BLOB(16777215)"));
        Assertions.assertThat(mediumblobCol.getTypeOriginal()).contains(Id.of("mediumblob"));

        // LONGBLOB
        val longblobCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("longblob_col"))
                .build());
        Assertions.assertThat(longblobCol.getType()).contains(Id.of("BLOB(2G)"));
        Assertions.assertThat(longblobCol.getTypeOriginal()).contains(Id.of("longblob"));

        // DATETIME
        val datetimeCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("datetime_col"))
                .build());
        Assertions.assertThat(datetimeCol.getType()).contains(Id.of("TIMESTAMP"));
        Assertions.assertThat(datetimeCol.getTypeOriginal()).contains(Id.of("datetime"));

        // TIMESTAMP
        val timestampCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("timestamp_col"))
                .build());
        Assertions.assertThat(timestampCol.getType()).contains(Id.of("TIMESTAMP"));
        Assertions.assertThat(timestampCol.getTypeOriginal()).contains(Id.of("timestamp"));

        // DATE
        val dateCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("date_col"))
                .build());
        Assertions.assertThat(dateCol.getType()).contains(Id.of("DATE"));
        Assertions.assertThat(dateCol.getTypeOriginal()).contains(Id.of("date"));

        // TIME
        val timeCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("time_col"))
                .build());
        Assertions.assertThat(timeCol.getType()).contains(Id.of("TIME"));
        Assertions.assertThat(timeCol.getTypeOriginal()).contains(Id.of("time"));

        // YEAR
        val yearCol = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("datatype_mapping_test"))
                .tableId(Id.of("datatype_mapping_test_table"))
                .columnId(Id.of("year_col"))
                .build());
        Assertions.assertThat(yearCol.getType()).contains(Id.of("SMALLINT"));
        Assertions.assertThat(yearCol.getTypeOriginal()).contains(Id.of("year"));

    }
}
