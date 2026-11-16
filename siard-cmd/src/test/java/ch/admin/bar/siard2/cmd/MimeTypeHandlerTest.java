package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.api.Cell;
import ch.admin.bar.siard2.api.MetaColumn;
import org.apache.tika.Tika;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MimeTypeHandlerTest {

    private static Cell newCell(String columnName) {
        MetaColumn metaColumn = mock(MetaColumn.class);
        when(metaColumn.getName()).thenReturn(columnName);
        Cell cell = mock(Cell.class);
        when(cell.getMetaColumn()).thenReturn(metaColumn);
        return cell;
    }

    // lob streams are opened once by the caller and reused for writing the value:
    // mime type detection must not consume or close them (mssql-jdbc discards
    // streamed lob values after the first stream has been consumed and closed)
    @Test
    void addStreamResetsTheDetectionStream() throws Exception {
        byte[] content = "hello".getBytes();
        InputStream stream = new BufferedInputStream(new ByteArrayInputStream(content));

        MimeTypeHandler handler = new MimeTypeHandler(new Tika());
        handler.add(newCell("bin"), stream);

        byte[] after = stream.readAllBytes();
        assertEquals(new String(content), new String(after));
    }

    @Test
    void clearIndividualMimeTypesReleasesPerRecordEntries() throws IOException {
        Cell cell = newCell("bin");
        MimeTypeHandler handler = new MimeTypeHandler(new Tika());

        handler.add(cell, "hello".getBytes());
        handler.applyMimeType(cell);
        // the individual mime type was applied to the cell's meta column
        verify(cell.getMetaColumn()).setMimeType("text/plain");

        handler.clearIndividualMimeTypes();

        // after clearing, a different cell in the same column no longer has an
        // individual entry and falls back to the column-level mime type
        Cell other = newCell("bin");
        handler.applyMimeType(other);
        verify(other.getMetaColumn()).setMimeType("text/plain");
    }
}
