package ch.admin.bar.siard2.api.primary;

import ch.admin.bar.siard2.api.Cell;
import ch.admin.bar.siard2.api.MetaColumn;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SetBigDecimalTest {

    @Mock
    TableRecordImpl tableRecord;

    @Mock
    MetaColumn metaColumn;

    private Logger valueLogger;
    private Level previousLevel;
    private Handler logHandler;
    private final List<LogRecord> logRecords = new ArrayList<>();

    @BeforeEach
    void setUp() throws IOException {
        doReturn(Types.NUMERIC).when(metaColumn)
                             .getPreType();
        doReturn("COLUMN1").when(metaColumn)
                           .getName();
        doReturn("DECIMAL(12,6)").when(metaColumn)
                                 .getType();
        doReturn("NUMBER(12,6)").when(metaColumn)
                                .getTypeOriginal();

        valueLogger = Logger.getLogger(ValueImpl.class.getName());
        previousLevel = valueLogger.getLevel();
        valueLogger.setLevel(Level.ALL);
        logHandler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                logRecords.add(record);
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        };
        logHandler.setLevel(Level.ALL);
        valueLogger.addHandler(logHandler);
    }

    @AfterEach
    void tearDown() {
        valueLogger.removeHandler(logHandler);
        valueLogger.setLevel(previousLevel);
    }

    private Cell newNumericCell() throws IOException {
        return CellImpl.newInstance(tableRecord, 0, metaColumn, null);
    }

    private void assertNoWarnings() {
        assertTrue(logRecords.stream()
                             .noneMatch(record -> record.getLevel()
                                                        .intValue() >= Level.WARNING.intValue()),
                "unexpected warnings logged: " + logRecords);
    }

    @Test
    @DisplayName("Writes a fractional BigDecimal as plain string without warning")
    void shouldWriteFractionalBigDecimalWithoutWarning() throws IOException {
        // regression test for https://github.com/sfa-siard/siard-suite/issues/181
        Cell cell = newNumericCell();
        BigDecimal value = new BigDecimal("74537.384597");

        cell.setBigDecimal(value);

        assertEquals("74537.384597", cell.getString());
        assertEquals(value, cell.getBigDecimal());
        assertNoWarnings();
    }

    @Test
    @DisplayName("Preserves scale of integral BigDecimal")
    void shouldPreserveScaleOfIntegralBigDecimal() throws IOException {
        Cell cell = newNumericCell();

        cell.setBigDecimal(new BigDecimal("5.000000"));

        assertEquals("5.000000", cell.getString());
        assertNoWarnings();
    }

    @Test
    @DisplayName("Writes a BigDecimal exceeding long range without warning")
    void shouldWriteBigDecimalExceedingLongRangeWithoutWarning() throws IOException {
        Cell cell = newNumericCell();
        BigDecimal value = new BigDecimal("123456789012345678901234567890.123");

        cell.setBigDecimal(value);

        assertEquals(value.toPlainString(), cell.getString());
        assertEquals(value, cell.getBigDecimal());
        assertNoWarnings();
    }

    @Test
    @DisplayName("Writes a small BigDecimal in plain notation without warning")
    void shouldWriteSmallBigDecimalInPlainNotation() throws IOException {
        Cell cell = newNumericCell();

        cell.setBigDecimal(new BigDecimal("0.0000001"));

        assertEquals("0.0000001", cell.getString());
        assertNoWarnings();
    }

    @Test
    @DisplayName("Writes an integral BigDecimal without warning")
    void shouldWriteIntegralBigDecimalWithoutWarning() throws IOException {
        Cell cell = newNumericCell();

        cell.setBigDecimal(BigDecimal.valueOf(42));

        assertEquals("42", cell.getString());
        assertNoWarnings();
    }

}
