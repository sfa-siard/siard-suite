package ch.admin.bar.siard2.cmd;


import ch.admin.bar.siard2.api.MetaColumn;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ObjectValueReaderTest {

    private ResultSet resultSet = mock(ResultSet.class);

    private MetaColumn metaColumn = mock(MetaColumn.class);

    private ObjectValueReader objectValueReader = spy(new ObjectValueReader(resultSet, metaColumn, 0));

    @Nested
    class Read_From_ResultSet {

        @Test
        void CHAR() throws IOException, SQLException {
            doReturn("CHAR").when(metaColumn)
                            .getTypeOriginal();
            doReturn(Types.CHAR).when(objectValueReader)
                                .getDataType(metaColumn);
            doReturn("a").when(resultSet)
                         .getString(0);

            assertEquals("a", objectValueReader.read());
        }

        @Test
        void VARCHAR() throws IOException, SQLException {
            doReturn("VARCHAR").when(metaColumn)
                               .getTypeOriginal();
            doReturn(Types.VARCHAR).when(objectValueReader)
                                   .getDataType(metaColumn);
            doReturn("abc").when(resultSet)
                           .getString(0);

            assertEquals("abc", objectValueReader.read());
        }

        @Test
        void CLOB() throws IOException, SQLException {
            doReturn("CLOB").when(metaColumn)
                            .getTypeOriginal();
            doReturn(Types.CLOB).when(objectValueReader)
                                .getDataType(metaColumn);
            Clob clob = mock(Clob.class);
            doReturn(clob).when(resultSet)
                          .getClob(0);

            assertEquals(clob, objectValueReader.read());
        }

        @Test
        void SQLXML() throws IOException, SQLException {
            doReturn("SQL_XML").when(metaColumn)
                               .getTypeOriginal();
            doReturn(Types.SQLXML).when(objectValueReader)
                                  .getDataType(metaColumn);
            SQLXML sqlxml = mock(SQLXML.class);
            doReturn(sqlxml).when(resultSet)
                            .getSQLXML(0);

            assertEquals(sqlxml, objectValueReader.read());
        }

        @Test
        void NCHAR() throws IOException, SQLException {
            doReturn("NCHAR").when(metaColumn)
                             .getTypeOriginal();
            doReturn(Types.NCHAR).when(objectValueReader)
                                 .getDataType(metaColumn);
            doReturn("nchar").when(resultSet)
                             .getNString(0);

            assertEquals("nchar", objectValueReader.read());
        }

        @Test
        void NVARCHAR() throws IOException, SQLException {
            doReturn("NVARCHAR").when(metaColumn)
                                .getTypeOriginal();
            doReturn(Types.NVARCHAR).when(objectValueReader)
                                    .getDataType(metaColumn);
            doReturn("NVARCHAR").when(resultSet)
                                .getNString(0);

            assertEquals("NVARCHAR", objectValueReader.read());
        }

        @Test
        void NCLOB() throws IOException, SQLException {
            doReturn("NCLOB").when(metaColumn)
                             .getTypeOriginal();
            doReturn(Types.NCLOB).when(objectValueReader)
                                 .getDataType(metaColumn);

            NClob nClob = mock(NClob.class);
            doReturn(nClob).when(resultSet)
                           .getNClob(0);

            assertEquals(nClob, objectValueReader.read());
        }

        @Test
        void BINARY() throws IOException, SQLException {
            doReturn("BINARY").when(metaColumn)
                              .getTypeOriginal();
            doReturn(Types.BINARY).when(objectValueReader)
                                  .getDataType(metaColumn);


            byte[] bytes = "abc".getBytes("UTF-8");
            doReturn(bytes).when(resultSet)
                           .getBytes(0);

            assertEquals(bytes, objectValueReader.read());
        }

        @Test
        void VARBINARY() throws IOException, SQLException {
            doReturn("VARBINARY").when(metaColumn)
                                 .getTypeOriginal();
            doReturn(Types.VARBINARY).when(objectValueReader)
                                     .getDataType(metaColumn);


            byte[] bytes = "abc".getBytes("UTF-8");
            doReturn(bytes).when(resultSet)
                           .getBytes(0);

            assertEquals(bytes, objectValueReader.read());
        }

        @Test
        void BLOB() throws IOException, SQLException {
            doReturn("BLOB").when(metaColumn)
                            .getTypeOriginal();
            doReturn(Types.BLOB).when(objectValueReader)
                                .getDataType(metaColumn);


            Blob blob = mock(Blob.class);
            doReturn(blob).when(resultSet)
                          .getBlob(0);

            assertEquals(blob, objectValueReader.read());
        }

        @Test
        void DATALINK() throws IOException, SQLException {
            doReturn("DATALINK").when(metaColumn)
                                .getTypeOriginal();

            doReturn(Types.DATALINK).when(objectValueReader)
                                    .getDataType(metaColumn);

            URL url = new URL("http://example.com");
            doReturn(url).when(resultSet)
                         .getURL(0);

            assertEquals(url, objectValueReader.read());
        }

        @Test
        void BOOLEAN() throws IOException, SQLException {
            doReturn("BOOLEAN").when(metaColumn)
                               .getTypeOriginal();
            doReturn(Types.BOOLEAN).when(objectValueReader)
                                   .getDataType(metaColumn);
            doReturn(true).when(resultSet)
                          .getBoolean(0);

            assertEquals(true, objectValueReader.read());
        }

        @Test
        void SMALLINT() throws IOException, SQLException {
            doReturn("SMALLINT").when(metaColumn)
                                .getTypeOriginal();
            doReturn(Types.SMALLINT).when(objectValueReader)
                                    .getDataType(metaColumn);
            doReturn(99).when(resultSet)
                        .getInt(0);

            assertEquals(99, objectValueReader.read());
        }

        @Test
        void INTEGER() throws IOException, SQLException {
            doReturn("INTEGER").when(metaColumn)
                               .getTypeOriginal();
            doReturn(Types.INTEGER).when(objectValueReader)
                                   .getDataType(metaColumn);
            doReturn(99L).when(resultSet)
                         .getLong(0);

            assertEquals(99L, objectValueReader.read());
        }

        @Test
        void BIGINT() throws IOException, SQLException {
            doReturn("BIGINT").when(metaColumn)
                              .getTypeOriginal();
            doReturn(Types.BIGINT).when(objectValueReader)
                                  .getDataType(metaColumn);
            doReturn(BigDecimal.valueOf(99L)).when(resultSet)
                                             .getBigDecimal(0);

            assertEquals(BigInteger.valueOf(99L), objectValueReader.read());

        }

        @Test
        void DECIMAL() throws IOException, SQLException {
            doReturn("DECIMAL").when(metaColumn)
                               .getTypeOriginal();
            doReturn(Types.DECIMAL).when(objectValueReader)
                                   .getDataType(metaColumn);
            doReturn(BigDecimal.ONE).when(resultSet)
                                    .getBigDecimal(0);

            assertEquals(BigDecimal.ONE, objectValueReader.read());
        }

        @Test
        void REAL() throws IOException, SQLException {
            doReturn("REAL").when(metaColumn)
                            .getTypeOriginal();
            doReturn(Types.REAL).when(objectValueReader)
                                .getDataType(metaColumn);
            doReturn(1.0f).when(resultSet)
                          .getFloat(0);

            assertEquals(1.0f, objectValueReader.read());
        }

        @Test
        void FLOAT() throws IOException, SQLException {
            doReturn("FLOAT").when(metaColumn)
                             .getTypeOriginal();
            doReturn(Types.FLOAT).when(objectValueReader)
                                 .getDataType(metaColumn);
            doReturn(1.0).when(resultSet)
                         .getDouble(0);

            assertEquals(1.0, objectValueReader.read());
        }

        @Test
        void DOUBLE() throws IOException, SQLException {
            doReturn("DOUBLE").when(metaColumn)
                              .getTypeOriginal();
            doReturn(Types.DOUBLE).when(objectValueReader)
                                  .getDataType(metaColumn);
            doReturn(1.0).when(resultSet)
                         .getDouble(0);

            assertEquals(1.0, objectValueReader.read());
        }

        @Test
        void DATE() throws IOException, SQLException {
            doReturn("DATE").when(metaColumn)
                            .getTypeOriginal();
            doReturn(Types.DATE).when(objectValueReader)
                                .getDataType(metaColumn);
            Date date = new Date(0);
            doReturn(date).when(resultSet)
                          .getDate(0);

            assertEquals(date, objectValueReader.read());
        }

        @Test
        void TIME() throws IOException, SQLException {
            doReturn("TIME").when(metaColumn)
                            .getTypeOriginal();
            doReturn(Types.TIME).when(objectValueReader)
                                .getDataType(metaColumn);
            Time time = new Time(0);
            doReturn(time).when(resultSet)
                          .getTime(0);

            assertEquals(time, objectValueReader.read());
        }

        @Test
        void TIMESTAMP() throws IOException, SQLException {
            doReturn("TIMESTAMP").when(metaColumn)
                                 .getTypeOriginal();
            doReturn(Types.TIMESTAMP).when(objectValueReader)
                                     .getDataType(metaColumn);
            Timestamp timestamp = new Timestamp(0);
            doReturn(timestamp).when(resultSet)
                               .getTimestamp(0);

            assertEquals(timestamp, objectValueReader.read());
        }

        @Test
        void OTHER() throws IOException, SQLException {
            doReturn("OTHER").when(metaColumn)
                             .getTypeOriginal();
            doReturn(Types.OTHER).when(objectValueReader)
                                 .getDataType(metaColumn);
            Object expected = new Object();
            doReturn(expected).when(resultSet)
                              .getObject(0);

            assertEquals(expected, objectValueReader.read());
        }

        @Test
        void STRUCT() throws IOException, SQLException {
            doReturn("STRUCT").when(metaColumn)
                              .getTypeOriginal();
            doReturn(Types.STRUCT).when(objectValueReader)
                                  .getDataType(metaColumn);
            Object expected = new Object();
            doReturn(expected).when(resultSet)
                              .getObject(0);

            assertEquals(expected, objectValueReader.read());
        }

        @Test
        void ARRAY() throws IOException, SQLException {
            doReturn("ARRAY").when(metaColumn)
                             .getTypeOriginal();
            doReturn(Types.ARRAY).when(objectValueReader)
                                 .getDataType(metaColumn);
            Array array = mock(Array.class);
            doReturn(array).when(resultSet)
                           .getArray(0);

            assertEquals(array, objectValueReader.read());
        }
    }

    @Nested
    class Special_Cases {
        @Test
        public void ignores_ROWID_and_returns_null() throws IOException, SQLException {
            doReturn("\"ROWID\"").when(metaColumn)
                                 .getTypeOriginal();
            assertNull(objectValueReader.read());
        }

        @Test
        public void resultSet_wasNull() throws IOException, SQLException {
            doReturn("VARCHAR").when(metaColumn)
                               .getTypeOriginal();
            doReturn(Types.VARCHAR).when(objectValueReader)
                                   .getDataType(metaColumn);
            doReturn(true).when(resultSet)
                          .wasNull();

            assertNull(objectValueReader.read());
        }

        @Test
        void throws_exception_when_data_type_is_unknown() throws IOException {
            doReturn("unknown").when(metaColumn)
                               .getTypeOriginal();
            doReturn(-99).when(objectValueReader)
                         .getDataType(metaColumn);
            assertThrows(SQLException.class, objectValueReader::read);
        }
    }
}