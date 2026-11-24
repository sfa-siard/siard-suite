package ch.admin.bar.siardsuite.webcore.archive;

import ch.admin.bar.siard2.api.Archive;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Exclusive use of an open archive for one request; closing it releases the archive's lock
 * (design D3), also from another thread than the one that acquired it. Closing more than once
 * has no effect.
 */
public final class ArchiveLease implements AutoCloseable {

    private final ArchiveRegistry.Entry entry;
    private final AtomicBoolean closed = new AtomicBoolean();

    ArchiveLease(ArchiveRegistry.Entry entry) {
        this.entry = entry;
    }

    public Archive archive() {
        return entry.archive;
    }

    RowCursors cursors() {
        return entry.cursors;
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            entry.lock.release();
        }
    }
}
