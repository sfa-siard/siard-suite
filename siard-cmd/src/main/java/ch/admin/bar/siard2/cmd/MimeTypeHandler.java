package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.api.*;
import org.apache.tika.Tika;

import java.io.IOException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.*;

/**
 * Understands how to set the mime type for a cell (aka. column) based on the actual content
 */
class MimeTypeHandler {

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

    public void add(Value value, Blob blob) throws SQLException, IOException {
        if (value instanceof Cell) this.add((Cell) value, blob);
        if (value instanceof Field) this.add((Field) value, blob);
    }

    public void add(Value value, byte[] bytes) throws IOException {
        if (value instanceof Cell) this.add((Cell) value, bytes);
        if (value instanceof Field) this.add((Field) value, bytes);
    }

    public void add(Value value, Clob clob) throws SQLException, IOException {
        if (value instanceof Cell) this.add((Cell) value, clob);
        if (value instanceof Field) this.add((Field) value, clob);
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

    public void applyColumnMimeType(Table table) throws IOException {
        for (int i = 0; i < table.getMetaTable().getMetaColumns(); i++) {
            MetaColumn metaColumn = table.getMetaTable().getMetaColumn(i);
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

    private void add(Cell cell, Clob clob) throws SQLException, IOException {
        String mimeType = tika.detect(clob.getAsciiStream());
        add(cell, mimeType);
        individualMimeTypes.put(cell, mimeType);
    }

    private void add(Field field, Clob clob) throws SQLException, IOException {
        String mimeType = tika.detect(clob.getAsciiStream());
        add(field, mimeType);
        individualMimeTypes.put(field, mimeType);
    }

    private void add(Cell cell, Blob blob) throws SQLException, IOException {
        String mimeType = tika.detect(blob.getBinaryStream());
        add(cell, mimeType);
        individualMimeTypes.put(cell, mimeType);
    }

    private void add(Field field, Blob blob) throws SQLException, IOException {
        String mimeType = tika.detect(blob.getBinaryStream());
        add(field, mimeType);
        individualMimeTypes.put(field, mimeType);
    }

    private void add(Cell cell, String mimeType) {
        add(mimeType, cell.getMetaColumn().getName());
    }

    private void add(Field field, String mimeType) {
        add(mimeType, field.getMetaField().getName());
    }

    private void add(String mimeType, String name) {
        if (!mimeTypes.containsKey(name)) {
            mimeTypes.put(name, new HashSet<>(Collections.singletonList(mimeType)));
        } else {
            mimeTypes.get(name).add(mimeType);
        }
    }

    private void applyMimeType(Cell cell) throws IOException {
        String individualMimeType = individualMimeTypes.get(cell);
        if (individualMimeType != null) {
            cell.getMetaColumn().setMimeType(individualMimeType);
            return;
        }
        applyMimeType(cell.getMetaColumn());
    }

    private void applyMimeType(Field field) throws IOException {
        String individualMimeType = individualMimeTypes.get(field);
        if (individualMimeType != null) {
            field.getMetaField().setMimeType(individualMimeType);
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
