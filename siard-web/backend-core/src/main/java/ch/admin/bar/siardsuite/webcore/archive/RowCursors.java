package ch.admin.bar.siardsuite.webcore.archive;

import ch.admin.bar.siard2.api.Table;
import ch.admin.bar.siard2.api.TableRecord;
import ch.admin.bar.siard2.api.TableRecordDispenser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The open record dispensers of one archive (design D4): at most {@value #MAX_PAGING} paging
 * cursors, least recently used first out. Only used under the archive's lock.
 */
final class RowCursors {

    private static final Logger LOG = LoggerFactory.getLogger(RowCursors.class);
    static final int MAX_PAGING = 2;

    private static final class Cursor {
        final Table table;
        final TableRecordDispenser dispenser;
        long position;

        Cursor(Table table) throws IOException {
            this.table = table;
            this.dispenser = table.openTableRecords();
        }
    }

    private final LinkedList<Cursor> paging = new LinkedList<>();
    // the search cursor, never used for paging, so a search keeps the paging positions (design D5)
    private Cursor search;
    private final AtomicLong recordsRead;

    RowCursors(AtomicLong recordsRead) {
        this.recordsRead = recordsRead;
    }

    /** Up to {@code limit} records from {@code offset}, reusing a cursor at or before it. */
    List<TableRecord> page(Table table, long offset, int limit) throws IOException {
        long total = table.getMetaTable().getRows();
        List<TableRecord> records = new ArrayList<>();
        if (offset >= total) {
            return records;
        }
        Cursor cursor = reusable(table, offset);
        if (cursor == null) {
            if (paging.size() >= MAX_PAGING) {
                close(paging.removeLast());
            }
            cursor = new Cursor(table);
        } else {
            paging.remove(cursor);
        }
        paging.addFirst(cursor);
        skip(cursor, offset);
        for (long i = 0; i < limit && cursor.position < total; i++) {
            records.add(read(cursor));
        }
        return records;
    }

    /**
     * Positions the search cursor at {@code start} of {@code table}, reusing it when it is on that
     * table at or before {@code start}.
     */
    void startSearch(Table table, long start) throws IOException {
        if (search == null || search.table != table || search.position > start) {
            if (search != null) {
                close(search);
            }
            search = new Cursor(table);
        }
        skip(search, start);
    }

    /** The next record of the search cursor; its row number is {@link #searchPosition()} before the call. */
    TableRecord nextSearchRecord() throws IOException {
        return read(search);
    }

    long searchPosition() {
        return search.position;
    }

    private Cursor reusable(Table table, long offset) {
        Cursor best = null;
        for (Cursor cursor : paging) {
            if (cursor.table == table && cursor.position <= offset && (best == null || cursor.position > best.position)) {
                best = cursor;
            }
        }
        return best;
    }

    private void skip(Cursor cursor, long to) throws IOException {
        long n = to - cursor.position;
        if (n > 0) {
            cursor.dispenser.skip(n);
            cursor.position = to;
            recordsRead.addAndGet(n);
        }
    }

    private TableRecord read(Cursor cursor) throws IOException {
        TableRecord record = cursor.dispenser.get();
        cursor.position++;
        recordsRead.incrementAndGet();
        return record;
    }

    void closeAll() {
        if (search != null) {
            close(search);
            search = null;
        }
        paging.forEach(RowCursors::close);
        paging.clear();
    }

    private static void close(Cursor cursor) {
        try {
            cursor.dispenser.close();
        } catch (IOException e) {
            LOG.warn("Record dispenser could not be closed", e);
        }
    }
}
