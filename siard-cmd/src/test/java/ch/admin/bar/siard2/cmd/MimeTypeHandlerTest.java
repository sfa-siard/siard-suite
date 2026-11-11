package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.api.Cell;
import ch.admin.bar.siard2.api.MetaColumn;
import org.apache.tika.Tika;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

    @Test
    void addBlobClosesTheDetectionStream() throws Exception {
        InputStream stream = mock(InputStream.class);
        when(stream.read(any(byte[].class), anyInt(), anyInt())).thenReturn(-1);
        Blob blob = mock(Blob.class);
        when(blob.getBinaryStream()).thenReturn(stream);

        MimeTypeHandler handler = new MimeTypeHandler(new Tika());
        handler.add(newCell("bin"), blob);

        verify(stream).close();
    }

    @Test
    void addBlobClosesTheStreamReturnedByGetBinaryStream() throws Exception {
        boolean[] closed = {false};
        InputStream stream = new ByteArrayInputStream("hello".getBytes()) {
            @Override
            public void close() throws IOException {
                closed[0] = true;
                super.close();
            }
        };
        Blob blob = mock(Blob.class);
        when(blob.getBinaryStream()).thenReturn(stream);

        MimeTypeHandler handler = new MimeTypeHandler(new Tika());
        handler.add(newCell("bin"), blob);

        assertTrue(closed[0], "detection stream must be closed");
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
