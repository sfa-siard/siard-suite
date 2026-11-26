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
        if (pointsOutsideTheArchive(cell)) {
            return new TextCell("", false);
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
        if (pointsOutsideTheArchive(cell)) {
            return "";
        }
        if (cell.getMetaValue().getPreType() == Types.DATALINK) {
            return dataLinkText(cell, maxChars);
        }
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

    /**
     * A DATALINK as the desktop app shows it ({@code RowsOverviewForm.extractText}): its text read with
     * {@code getString()}, or empty when that fails, e.g. for a file that is not text, whose character count
     * {@code siard-api} rejects. Per cell, so one such file does not fail the page.
     */
    private static String dataLinkText(Cell cell, int maxChars) {
        try {
            String text = cell.getString();
            if (text == null) {
                return "";
            }
            return text.length() > maxChars ? text.substring(0, maxChars) : text;
        } catch (IOException e) {
            return "";
        }
    }

    static boolean isBinary(Cell cell) throws IOException {
        return isBinaryType(cell.getMetaValue().getPreType()) && !pointsOutsideTheArchive(cell);
    }

    /**
     * Whether the cell points outside the archive by URL ({@code dlurlpathonly}); it is then shown empty and its
     * content never read. {@code siard-api} opens that URL before a stored file in {@code getInputStream()} and
     * {@code getReader()} (also behind {@code getString()} and {@code getBytes()}), also when the cell has both,
     * so rows, download and search check this first. Any type: the schema allows the attribute on binary and
     * text LOBs. The one deliberate deviation from the desktop app (design.md, follow-up changes).
     */
    static boolean pointsOutsideTheArchive(Cell cell) throws IOException {
        return cell.getDataLinkUrl() != null;
    }

    static boolean isBinaryType(int type) {
        return type == Types.BINARY || type == Types.VARBINARY || type == Types.BLOB;
    }

    /** Large text types, read as a prefix instead of as a whole. */
    static boolean isReaderType(Cell cell) throws IOException {
        int type = cell.getMetaValue().getPreType();
        return (type == Types.CLOB || type == Types.NCLOB || type == Types.SQLXML) && !pointsOutsideTheArchive(cell);
    }
}
