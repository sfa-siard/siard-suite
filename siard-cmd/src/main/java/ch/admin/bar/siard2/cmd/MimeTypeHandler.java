package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.api.*;
import org.apache.tika.Tika;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Understands how to set the mime type for a cell (aka. column) based on the actual content
 */
class MimeTypeHandler {

    // number of characters read for mime type detection on character lob streams
    private static final int MIME_DETECTION_PREFIX_CHARS = 8192;

    // used to keep track of detected mime types per column (name)
    private final Map<String, Set<String>> mimeTypes;

    // used to keep track of detected mime types per individual record
    private final Map<Value, String> individualMimeTypes;

    // tika is able to detect a mime type from byte[] or input streams (and more)
    private final Tika tika;

    public MimeTypeHandler(Tika tika) {
        this.mimeTypes = new HashMap<>();
        this.individualMimeTypes = new HashMap<>();
        this.tika = tika;
    }

    /**
     * Detects the mime type from an already opened binary lob stream. The stream must
     * support marks (e.g. a {@link java.io.BufferedInputStream}); it is reset after
     * detection so it can be reused for writing the actual value. The stream is NOT
     * closed by this method.
     */
    public void add(Value value, InputStream stream) throws IOException {
        if (value instanceof Cell) this.add((Cell) value, stream);
        if (value instanceof Field) this.add((Field) value, stream);
    }

    public void add(Value value, byte[] bytes) throws IOException {
        if (value instanceof Cell) this.add((Cell) value, bytes);
        if (value instanceof Field) this.add((Field) value, bytes);
    }

    /**
     * Detects the mime type from an already opened character lob stream. The reader must
     * support marks (e.g. a {@link java.io.BufferedReader}); it is reset after detection
     * so it can be reused for writing the actual value. The reader is NOT closed by
     * this method.
     */
    public void add(Value value, Reader reader) throws IOException {
        if (value instanceof Cell) this.add((Cell) value, reader);
        if (value instanceof Field) this.add((Field) value, reader);
    }

    /**
     * Applies the most suitable mime type to the cells meta column.
     * Most suitable means:
     * - The meta column should contain the mime type if exactly 1 mime type is present in the column
     * - For mixed content columns, each individual record gets its own detected MIME type
     *
     * @param value - the value to apply the mime type to
     */
    public void applyMimeType(Value value) throws IOException {
        if (value instanceof Cell) this.applyMimeType((Cell) value);
        if (value instanceof Field) this.applyMimeType((Field) value);
    }

    /**
     * Clears the per-record mime type entries. Entries are only needed until
     * their record has been written; retaining them for the whole table would
     * keep every processed cell object reachable.
     */
    public void clearIndividualMimeTypes() {
        individualMimeTypes.clear();
    }

    public void applyColumnMimeType(Table table) throws IOException {
        for (int i = 0; i < table.getMetaTable()
                                 .getMetaColumns(); i++) {
            MetaColumn metaColumn = table.getMetaTable()
                                         .getMetaColumn(i);
            applyMimeType(metaColumn);
        }
    }

    private void add(Cell cell, byte[] bytes) {
        String mimeType = tika.detect(bytes);
        add(cell, mimeType);
        individualMimeTypes.put(cell, mimeType);
    }

    private void add(Field field, byte[] bytes) {
        String mimeType = tika.detect(bytes);
        add(field, mimeType);
        individualMimeTypes.put(field, mimeType);
    }

    private void add(Cell cell, Reader reader) throws IOException {
        String mimeType = detect(reader);
        add(cell, mimeType);
        individualMimeTypes.put(cell, mimeType);
    }

    private void add(Field field, Reader reader) throws IOException {
        String mimeType = detect(reader);
        add(field, mimeType);
        individualMimeTypes.put(field, mimeType);
    }

    private void add(Cell cell, InputStream stream) throws IOException {
        String mimeType = tika.detect(stream);
        add(cell, mimeType);
        individualMimeTypes.put(cell, mimeType);
    }

    private void add(Field field, InputStream stream) throws IOException {
        String mimeType = tika.detect(stream);
        add(field, mimeType);
        individualMimeTypes.put(field, mimeType);
    }

    private String detect(Reader reader) throws IOException {
        reader.mark(MIME_DETECTION_PREFIX_CHARS);
        char[] buffer = new char[MIME_DETECTION_PREFIX_CHARS];
        int read = 0;
        while (read < buffer.length) {
            int n = reader.read(buffer, read, buffer.length - read);
            if (n < 0) break;
            read += n;
        }
        reader.reset();
        return tika.detect(new String(buffer, 0, read).getBytes(StandardCharsets.UTF_8));
    }

    private void add(Cell cell, String mimeType) {
        add(mimeType, cell.getMetaColumn()
                          .getName());
    }

    private void add(Field field, String mimeType) {
        add(mimeType, field.getMetaField()
                           .getName());
    }

    private void add(String mimeType, String name) {
        if (!mimeTypes.containsKey(name)) {
            mimeTypes.put(name, new HashSet<>(Collections.singletonList(mimeType)));
        } else {
            mimeTypes.get(name)
                     .add(mimeType);
        }
    }

    private void applyMimeType(Cell cell) throws IOException {
        String individualMimeType = individualMimeTypes.get(cell);
        if (individualMimeType != null) {
            cell.getMetaColumn()
                .setMimeType(individualMimeType);
            return;
        }
        applyMimeType(cell.getMetaColumn());
    }

    private void applyMimeType(Field field) throws IOException {
        String individualMimeType = individualMimeTypes.get(field);
        if (individualMimeType != null) {
            field.getMetaField()
                 .setMimeType(individualMimeType);
            return;
        }
        applyMimeType(field.getMetaField());
    }

    private void applyMimeType(MetaValue metaValue) throws IOException {
        Set<String> types = mimeTypes.get(metaValue.getName());
        if (types == null) return;
        if (types.size() == 1) metaValue.setMimeType((String) types.toArray()[0]);
        if (types.size() > 1) metaValue.setMimeType("mixed");
    }
}
