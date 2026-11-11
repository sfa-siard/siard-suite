package ch.admin.bar.siard2.jdbc;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Types;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Db2MetaColumnsTest {

    @Test
    public void longVarcharIsMappedToVarchar() throws SQLException {
        assertEquals(Types.VARCHAR,
                     Db2MetaColumns.getDataType(Types.LONGVARCHAR, "LONG VARCHAR"));
    }

    @Test
    public void longVarcharForBitDataIsMappedToVarbinary() throws SQLException {
        assertEquals(Types.VARBINARY,
                     Db2MetaColumns.getDataType(Types.LONGVARBINARY, "LONG VARCHAR FOR BIT DATA"));
    }
}
