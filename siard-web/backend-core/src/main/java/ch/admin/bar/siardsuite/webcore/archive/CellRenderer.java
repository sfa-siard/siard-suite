package ch.admin.bar.siardsuite.webcore.archive;

import ch.admin.bar.siard2.api.Cell;
import ch.admin.bar.siardsuite.webcore.archive.ArchiveDtos.CellDto;
import ch.admin.bar.siardsuite.webcore.archive.ArchiveDtos.LobCell;
import ch.admin.bar.siardsuite.webcore.archive.ArchiveDtos.NullCell;
import ch.admin.bar.siardsuite.webcore.archive.ArchiveDtos.TextCell;

import java.io.IOException;
import java.io.Reader;
import java.sql.Types;

/**
 * Renders a cell the way the desktop app shows it (design D6): binary types are large objects,
 * everything else is the cell's string form, cut at {@value #MAX_TEXT} characters.
 */
final class CellRenderer {

    static final int MAX_TEXT = 4096;

    private CellRenderer() {
    }

    /** The cell as DTO. */
    static CellDto render(Cell cell) throws IOException {
        if (cell.isNull()) {
            return new NullCell();
        }
        if (isBinary(cell)) {
            return new LobCell(cell.getByteLength());
        }
        String text = text(cell, MAX_TEXT + 1);
        return text.length() > MAX_TEXT
                ? new TextCell(text.substring(0, MAX_TEXT), true)
                : new TextCell(text, false);
    }

    /** The displayed text of a non-binary, non-NULL cell, reading at most {@code maxChars} characters. */
    static String text(Cell cell, int maxChars) throws IOException {
        if (isReaderType(cell)) {
            try (Reader reader = cell.getReader()) {
                StringBuilder text = new StringBuilder();
                char[] buffer = new char[Math.min(maxChars, 8192)];
                for (int n; text.length() < maxChars
                        && (n = reader.read(buffer, 0, Math.min(buffer.length, maxChars - text.length()))) > 0; ) {
                    text.append(buffer, 0, n);
                }
                return text.toString();
            }
        }
        String text = cell.getString();
        return text == null ? "" : text;
    }

    static boolean isBinary(Cell cell) throws IOException {
        return isBinaryType(cell.getMetaValue().getPreType());
    }

    static boolean isBinaryType(int type) {
        return type == Types.BINARY || type == Types.VARBINARY || type == Types.BLOB;
    }

    /** Large text types, read as a prefix instead of as a whole. */
    static boolean isReaderType(Cell cell) throws IOException {
        int type = cell.getMetaValue().getPreType();
        return type == Types.CLOB || type == Types.NCLOB || type == Types.SQLXML;
    }
}
