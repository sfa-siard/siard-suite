package ch.admin.bar.siard2.api;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import javax.xml.datatype.Duration;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ValueTest {

    @Spy
    Value value;

    @Mock
    MetaValue metaValue;

    @BeforeEach
    void setUp() {
        doReturn(metaValue).when(value)
                           .getMetaValue();
    }

    @Test
    @DisplayName("Converts a smallint value")
    public void shouldConvertValue_SmallInt() throws IOException {
        doReturn(1).when(value)
                   .getInt();
        doReturn(Types.SMALLINT).when(metaValue)
                                .getPreType();

        String result = value.convert();

        Assertions.assertEquals("1", result);
    }

    @Test
    @DisplayName("Converts a char value")
    public void shouldConvertValue_Char() throws IOException {
        doReturn("test").when(value)
                        .getString();
        doReturn(Types.CHAR).when(metaValue)
                            .getPreType();

        String result = value.convert();

        Assertions.assertEquals("test", result);
    }

    @Test
    @DisplayName("Converts a varchar value")
    public void shouldConvertValue_VarChar() throws IOException {
        doReturn("test string").when(value)
                               .getString();
        doReturn(Types.VARCHAR).when(metaValue)
                               .getPreType();

        String result = value.convert();

        Assertions.assertEquals("test string", result);
    }

    @Test
    @DisplayName("Converts a nchar value")
    public void shouldConvertValue_NChar() throws IOException {
        doReturn("unicode").when(value)
                           .getString();
        doReturn(Types.NCHAR).when(metaValue)
                             .getPreType();

        String result = value.convert();

        Assertions.assertEquals("unicode", result);
    }

    @Test
    @DisplayName("Converts a nvarchar value")
    public void shouldConvertValue_NVarChar() throws IOException {
        doReturn("unicode string").when(value)
                                  .getString();
        doReturn(Types.NVARCHAR).when(metaValue)
                                .getPreType();

        String result = value.convert();

        Assertions.assertEquals("unicode string", result);
    }

    @Test
    @DisplayName("Converts a clob value")
    public void shouldConvertValue_Clob() throws IOException {
        doReturn("large text").when(value)
                              .getString();
        doReturn(Types.CLOB).when(metaValue)
                            .getPreType();

        String result = value.convert();

        Assertions.assertEquals("large text", result);
    }

    @Test
    @DisplayName("Converts a nclob value")
    public void shouldConvertValue_NClob() throws IOException {
        doReturn("large unicode text").when(value)
                                      .getString();
        doReturn(Types.NCLOB).when(metaValue)
                             .getPreType();

        String result = value.convert();

        Assertions.assertEquals("large unicode text", result);
    }

    @Test
    @DisplayName("Converts a sqlxml value")
    public void shouldConvertValue_SqlXml() throws IOException {
        doReturn("<xml>content</xml>").when(value)
                                      .getString();
        doReturn(Types.SQLXML).when(metaValue)
                              .getPreType();

        String result = value.convert();

        Assertions.assertEquals("<xml>content</xml>", result);
    }

    @Test
    @DisplayName("Converts a datalink value")
    public void shouldConvertValue_DataLink() throws IOException {
        doReturn("http://example.com").when(value)
                                      .getString();
        doReturn(Types.DATALINK).when(metaValue)
                                .getPreType();

        String result = value.convert();

        Assertions.assertEquals("http://example.com", result);
    }

    @Test
    @DisplayName("Converts a binary value")
    public void shouldConvertValue_Binary() throws IOException {
        byte[] bytes = {0x01, 0x02, 0x03};
        doReturn(bytes).when(value)
                       .getBytes();
        doReturn(Types.BINARY).when(metaValue)
                              .getPreType();

        String result = value.convert();

        Assertions.assertEquals("0x010203", result);
    }

    @Test
    @DisplayName("Converts a varbinary value")
    public void shouldConvertValue_VarBinary() throws IOException {
        byte[] bytes = {(byte) 0xFF, (byte) 0xAB};
        doReturn(bytes).when(value)
                       .getBytes();
        doReturn(Types.VARBINARY).when(metaValue)
                                 .getPreType();

        String result = value.convert();

        Assertions.assertEquals("0xFFAB", result);
    }

    @Test
    @DisplayName("Converts a blob value")
    public void shouldConvertValue_Blob() throws IOException {
        byte[] bytes = {0x00, 0x11, 0x22};
        doReturn(bytes).when(value)
                       .getBytes();
        doReturn(Types.BLOB).when(metaValue)
                            .getPreType();

        String result = value.convert();

        Assertions.assertEquals("0x001122", result);
    }

    @Test
    @DisplayName("Converts a numeric value")
    public void shouldConvertValue_Numeric() throws IOException {
        BigDecimal decimal = new BigDecimal("123.456");
        doReturn(decimal).when(value)
                         .getBigDecimal();
        doReturn(Types.NUMERIC).when(metaValue)
                               .getPreType();

        String result = value.convert();

        Assertions.assertEquals("123.456", result);
    }

    @Test
    @DisplayName("Converts a decimal value")
    public void shouldConvertValue_Decimal() throws IOException {
        BigDecimal decimal = new BigDecimal("999.99");
        doReturn(decimal).when(value)
                         .getBigDecimal();
        doReturn(Types.DECIMAL).when(metaValue)
                               .getPreType();

        String result = value.convert();

        Assertions.assertEquals("999.99", result);
    }

    @Test
    @DisplayName("Converts an integer value")
    public void shouldConvertValue_Integer() throws IOException {
        doReturn(42L).when(value)
                     .getLong();
        doReturn(Types.INTEGER).when(metaValue)
                               .getPreType();

        String result = value.convert();

        Assertions.assertEquals("42", result);
    }

    @Test
    @DisplayName("Converts a bigint value")
    public void shouldConvertValue_BigInt() throws IOException {
        BigInteger bigInt = new BigInteger("9223372036854775807");
        doReturn(bigInt).when(value)
                        .getBigInteger();
        doReturn(Types.BIGINT).when(metaValue)
                              .getPreType();

        String result = value.convert();

        Assertions.assertEquals("9223372036854775807", result);
    }

    @Test
    @DisplayName("Converts a float value")
    public void shouldConvertValue_Float() throws IOException {
        doReturn(3.14159).when(value)
                         .getDouble();
        doReturn(Types.FLOAT).when(metaValue)
                             .getPreType();

        String result = value.convert();

        Assertions.assertEquals("3.14159", result);
    }

    @Test
    @DisplayName("Converts a double value")
    public void shouldConvertValue_Double() throws IOException {
        doReturn(2.718281828).when(value)
                             .getDouble();
        doReturn(Types.DOUBLE).when(metaValue)
                              .getPreType();

        String result = value.convert();

        Assertions.assertEquals("2.718281828", result);
    }

    @Test
    @DisplayName("Converts a real value")
    public void shouldConvertValue_Real() throws IOException {
        doReturn(1.414f).when(value)
                        .getFloat();
        doReturn(Types.REAL).when(metaValue)
                            .getPreType();

        String result = value.convert();

        Assertions.assertEquals("1.414", result);
    }

    @Test
    @DisplayName("Converts a boolean true value")
    public void shouldConvertValue_BooleanTrue() throws IOException {
        doReturn("true").when(value)
                        .getString();
        doReturn(true).when(value)
                      .getBoolean();
        doReturn(Types.BOOLEAN).when(metaValue)
                               .getPreType();

        String result = value.convert();

        Assertions.assertEquals("true", result);
    }

    @Test
    @DisplayName("Converts a boolean false value")
    public void shouldConvertValue_BooleanFalse() throws IOException {
        doReturn("false").when(value)
                         .getString();
        doReturn(false).when(value)
                       .getBoolean();
        doReturn(Types.BOOLEAN).when(metaValue)
                               .getPreType();

        String result = value.convert();

        Assertions.assertEquals("false", result);
    }

    @Test
    @DisplayName("Converts a boolean null value")
    public void shouldConvertValue_BooleanNull() throws IOException {
        doReturn("").when(value)
                    .getString();
        doReturn(Types.BOOLEAN).when(metaValue)
                               .getPreType();

        String result = value.convert();

        Assertions.assertEquals("", result);
    }

    @Test
    @DisplayName("Converts a date value")
    public void shouldConvertValue_Date() throws IOException {
        Date date = Date.valueOf("2023-12-25");
        doReturn(date).when(value)
                      .getDate();
        doReturn(Types.DATE).when(metaValue)
                            .getPreType();

        String result = value.convert();

        // The actual format depends on DU.getInstance() implementation
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Converts a time value")
    public void shouldConvertValue_Time() throws IOException {
        Time time = Time.valueOf("14:30:00");
        doReturn(time).when(value)
                      .getTime();
        doReturn(Types.TIME).when(metaValue)
                            .getPreType();

        String result = value.convert();

        // The actual format depends on DU.getInstance() implementation
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Converts a timestamp value")
    public void shouldConvertValue_Timestamp() throws IOException {
        Timestamp timestamp = Timestamp.valueOf("2023-12-25 14:30:00");
        doReturn(timestamp).when(value)
                           .getTimestamp();
        doReturn(Types.TIMESTAMP).when(metaValue)
                                 .getPreType();

        String result = value.convert();

        // The actual format depends on DU.getInstance() implementation
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Converts an other (interval) value")
    public void shouldConvertValue_Other() throws IOException {
        Duration duration = mock(Duration.class);
        doReturn(60).when(duration)
                    .getMinutes();
        doReturn(86000000L).when(duration)
                           .getTimeInMillis(new java.util.Date(0L));
        doReturn(duration).when(value)
                          .getDuration();
        doReturn(Types.OTHER).when(metaValue)
                             .getPreType();

        String result = value.convert();

        // The actual format depends on SqlLiterals.formatIntervalLiteral() implementation
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Converts unknown type to empty string")
    public void shouldConvertValue_UnknownType() throws IOException {
        doReturn(999).when(metaValue)
                     .getPreType(); // Unknown type

        String result = value.convert();

        Assertions.assertEquals("", result);
    }

    @Test
    @DisplayName("Converts a null value")
    public void shouldConvertValue_Null() throws IOException {
        doReturn(true).when(value)
                      .isNull();
        String result = value.convert();

        Assertions.assertEquals("", result);
    }

}