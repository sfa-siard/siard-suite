package ch.admin.bar.siard2.cmd.msaccess.issues.siardsuite163;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.header.Metadata;
import ch.admin.bar.siard2.cmd.utils.siard.utils.MetadataExplorer;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedColumnId;
import lombok.val;
import org.assertj.core.api.SoftAssertions;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

public class MSAccessDataTypeMappingIT {

    private static final String SCHEMA = "Admin";
    private static final String TABLE_COMPLEX = "TABLETEST";
    private static final String TABLE_SIMPLE = "TACCESSSIMPLE";

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Test
    public void downloadArchive() throws SQLException, IOException, ClassNotFoundException {
        val dbPath = "testfiles/testaccess.accdb";

        val siardArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb dbToSiard = new SiardFromDb(new String[]{
                "-o",
                "-j:" + "jdbc:access:" + dbPath,
                "-u:Admin",
                "-p:pw",
                "-s:" + siardArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardFromDb.iRETURN_OK, dbToSiard.getReturn());

        val metadataExplorer = siardArchive.exploreMetadata();
        val softly = new SoftAssertions();

        // ===== TABLETEST =====

        // TEXT (column COLTEXT)
        val textCol = findColumn(metadataExplorer, TABLE_COMPLEX, "COLTEXT");
        softly.assertThat(textCol.getTypeOriginal()).contains(Id.of("TEXT"));
        softly.assertThat(textCol.getType()).contains(Id.of("VARCHAR(255)"));

        // MEMO (column COLMEMO)
        val memoCol = findColumn(metadataExplorer, TABLE_COMPLEX, "COLMEMO");
        softly.assertThat(memoCol.getTypeOriginal()).contains(Id.of("MEMO"));
        softly.assertThat(memoCol.getType()).contains(Id.of("CLOB"));

        // LONG (column COLLONG)
        val longCol = findColumn(metadataExplorer, TABLE_COMPLEX, "COLLONG");
        softly.assertThat(longCol.getTypeOriginal()).contains(Id.of("LONG"));
        softly.assertThat(longCol.getType()).contains(Id.of("INT"));

        // INT (column COLINT)
        val intCol = findColumn(metadataExplorer, TABLE_COMPLEX, "COLINT");
        softly.assertThat(intCol.getTypeOriginal()).contains(Id.of("INT"));
        softly.assertThat(intCol.getType()).contains(Id.of("SMALLINT"));

        // DECIMAL (column COLDECIMAL)
        val decimalCol = findColumn(metadataExplorer, TABLE_COMPLEX, "COLDECIMAL");
        softly.assertThat(decimalCol.getTypeOriginal()).contains(Id.of("NUMERIC"));
        softly.assertThat(decimalCol.getType()).contains(Id.of("NUMERIC(18)"));

        // DOUBLE (column COLDOUBLE)
        val doubleCol = findColumn(metadataExplorer, TABLE_COMPLEX, "COLDOUBLE");
        softly.assertThat(doubleCol.getTypeOriginal()).contains(Id.of("DOUBLE"));
        softly.assertThat(doubleCol.getType()).contains(Id.of("DOUBLE PRECISION"));

        // FLOAT (column COLFLOAT)
        val floatCol = findColumn(metadataExplorer, TABLE_COMPLEX, "COLFLOAT");
        softly.assertThat(floatCol.getTypeOriginal()).contains(Id.of("FLOAT"));
        softly.assertThat(floatCol.getType()).contains(Id.of("REAL"));

        // SHORT_DATE_TIME (column COLDATETIME)
        val datetimeCol = findColumn(metadataExplorer, TABLE_COMPLEX, "COLDATETIME");
        softly.assertThat(datetimeCol.getTypeOriginal()).contains(Id.of("SHORT_DATE_TIME"));
        softly.assertThat(datetimeCol.getType()).contains(Id.of("TIMESTAMP"));

        // MONEY (column COLMONEY)
        val moneyCol = findColumn(metadataExplorer, TABLE_COMPLEX, "COLMONEY");
        softly.assertThat(moneyCol.getTypeOriginal()).contains(Id.of("MONEY"));
        softly.assertThat(moneyCol.getType()).contains(Id.of("DEC(19, 4)"));

        // BOOLEAN (column COLBOOLEAN)
        val booleanCol = findColumn(metadataExplorer, TABLE_COMPLEX, "COLBOOLEAN");
        softly.assertThat(booleanCol.getTypeOriginal()).contains(Id.of("BOOLEAN"));
        softly.assertThat(booleanCol.getType()).contains(Id.of("BOOLEAN"));

        // HYPERLINK (column COLLINK)
        val hyperlinkCol = findColumn(metadataExplorer, TABLE_COMPLEX, "COLLINK");
        softly.assertThat(hyperlinkCol.getTypeOriginal()).contains(Id.of("MEMO"));
        softly.assertThat(hyperlinkCol.getType()).contains(Id.of("CLOB"));

        // ATTACHMENT (column COLATTACH)
        val attachmentCol = findColumn(metadataExplorer, TABLE_COMPLEX, "COLATTACH");
        softly.assertThat(attachmentCol.getTypeOriginal()).contains(Id.of("BLOB ARRAY[127]"));
        softly.assertThat(attachmentCol.getType()).contains(Id.of("BLOB"));

        // ===== TACCESSSIMPLE =====

        // COUNTER (column CCOUNTER)
        val counterCol = findColumn(metadataExplorer, TABLE_SIMPLE, "CCOUNTER");
        softly.assertThat(counterCol.getTypeOriginal()).contains(Id.of("LONG"));
        softly.assertThat(counterCol.getType()).contains(Id.of("INT"));

        // CHAR(n) (column CCHAR_254)
        val charNCol = findColumn(metadataExplorer, TABLE_SIMPLE, "CCHAR_254");
        softly.assertThat(charNCol.getTypeOriginal()).contains(Id.of("TEXT"));
        softly.assertThat(charNCol.getType()).contains(Id.of("VARCHAR(254)"));

        // VARCHAR(n) (column CVARCHAR_254)
        val varcharNCol = findColumn(metadataExplorer, TABLE_SIMPLE, "CVARCHAR_254");
        softly.assertThat(varcharNCol.getTypeOriginal()).contains(Id.of("TEXT"));
        softly.assertThat(varcharNCol.getType()).contains(Id.of("VARCHAR(254)"));

        // GUID (column CGUID)
        val guidCol = findColumn(metadataExplorer, TABLE_SIMPLE, "CGUID");
        softly.assertThat(guidCol.getTypeOriginal()).contains(Id.of("GUID"));
        softly.assertThat(guidCol.getType()).contains(Id.of("CHAR(38)"));

        // BINARY (column CBINARY)
        val binaryCol = findColumn(metadataExplorer, TABLE_SIMPLE, "CBINARY");
        softly.assertThat(binaryCol.getTypeOriginal()).contains(Id.of("BINARY"));
        softly.assertThat(binaryCol.getType()).contains(Id.of("BINARY(510)"));

        // VARBINARY (column CVARBINARY)
        val varbinaryCol = findColumn(metadataExplorer, TABLE_SIMPLE, "CVARBINARY");
        softly.assertThat(varbinaryCol.getTypeOriginal()).contains(Id.of("BINARY"));
        softly.assertThat(varbinaryCol.getType()).contains(Id.of("BINARY(510)"));

        // DECIMAL(p,q) (column CDECIMAL_10_5)
        val decimalPqCol = findColumn(metadataExplorer, TABLE_SIMPLE, "CDECIMAL_10_5");
        softly.assertThat(decimalPqCol.getTypeOriginal()).contains(Id.of("NUMERIC"));
        softly.assertThat(decimalPqCol.getType()).contains(Id.of("NUMERIC(10, 5)"));

        softly.assertAll();
    }

    private Metadata.Column findColumn(
            MetadataExplorer metadataExplorer,
            String tableName,
            String columnName) {
        return metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of(SCHEMA))
                .tableId(Id.of(tableName))
                .columnId(Id.of(columnName))
                .build());
    }
}
