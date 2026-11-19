package ch.admin.bar.siardsuite.webcore.archive;

import ch.admin.bar.siard2.api.Cell;
import ch.admin.bar.siard2.api.Table;
import ch.admin.bar.siard2.api.facade.TableRecordFacade;
import ch.admin.bar.siardsuite.webcore.archive.ArchiveDtos.SearchDto;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Locale;

/**
 * One bounded search request (design D5): scans from a row on the archive's search cursor and
 * compares the displayed text of each cell, at most {@code maxRows} rows or {@code maxTime}.
 */
final class SearchScan {

    private SearchScan() {
    }

    static SearchDto run(RowCursors cursors, Table table, long start, String query, boolean matchCase,
                         long maxRows, Duration maxTime) throws IOException {
        long total = table.getMetaTable().getRows();
        if (start >= total) {
            return new SearchDto(false, null, null, true, null);
        }
        String needle = matchCase ? query : query.toLowerCase(Locale.ROOT);
        long deadline = System.nanoTime() + maxTime.toNanos();
        cursors.startSearch(table, start);
        for (long scanned = 0; cursors.searchPosition() < total; scanned++) {
            if (scanned >= maxRows || System.nanoTime() > deadline) {
                return new SearchDto(false, null, null, false, cursors.searchPosition());
            }
            long row = cursors.searchPosition();
            List<Cell> cells = new TableRecordFacade(cursors.nextSearchRecord()).getCells();
            for (int c = 0; c < cells.size(); c++) {
                if (matches(cells.get(c), needle, matchCase)) {
                    return new SearchDto(true, row, c, false, null);
                }
            }
        }
        return new SearchDto(false, null, null, true, null);
    }

    /** Whether the cell's displayed text (its first 4096 characters; binary cells have none) contains the needle. */
    private static boolean matches(Cell cell, String needle, boolean matchCase) throws IOException {
        if (cell.isNull() || CellRenderer.isBinary(cell)) {
            return false;
        }
        String text = CellRenderer.text(cell, CellRenderer.MAX_TEXT);
        if (text.length() > CellRenderer.MAX_TEXT) {
            text = text.substring(0, CellRenderer.MAX_TEXT);
        }
        return (matchCase ? text : text.toLowerCase(Locale.ROOT)).contains(needle);
    }
}
