package ch.admin.bar.siardsuite.webcore.archive;

import ch.admin.bar.siard2.api.Archive;

/**
 * Exclusive use of an open archive for one request; closing it releases the archive's lock
 * (design D3). Closing more than once has no effect.
 */
public final class ArchiveLease implements AutoCloseable {

    private final ArchiveRegistry.Entry entry;
    private boolean closed;

    ArchiveLease(ArchiveRegistry.Entry entry) {
        this.entry = entry;
    }

    public Archive archive() {
        return entry.archive;
    }

    @Override
    public void close() {
        if (!closed) {
            closed = true;
            entry.lock.unlock();
        }
    }
}
