package ch.admin.bar.siardsuite.webcore.archive;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siardsuite.webcore.error.ProblemException;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Holds the open archives of all scopes (design D1-D3): registration with limits, lookup by
 * {@code (scope, id)} under the archive's lock, explicit close, idle eviction and release on
 * shutdown. Every registered archive's {@code onRelease} runs exactly once, on whichever path
 * ends it.
 */
@ApplicationScoped
public class ArchiveRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(ArchiveRegistry.class);

    static final class Entry {
        final ArchiveId id;
        final ArchiveScope scope;
        final Archive archive;
        // a semaphore, not a ReentrantLock: a streamed download releases it on another thread (design D3)
        final Semaphore lock = new Semaphore(1);
        final Runnable onRelease;
        final RowCursors cursors;
        volatile Instant lastUsed;

        Entry(ArchiveId id, ArchiveScope scope, Archive archive, Runnable onRelease, Instant now,
              AtomicLong recordsRead) {
            this.id = id;
            this.scope = scope;
            this.archive = archive;
            this.onRelease = onRelease;
            this.lastUsed = now;
            this.cursors = new RowCursors(recordsRead);
        }
    }

    private final Map<ArchiveId, Entry> entries = new ConcurrentHashMap<>();
    // registrations counted against the limits while their archive is being opened
    private final Map<ArchiveScope, Integer> pending = new ConcurrentHashMap<>();
    private final AtomicLong recordsRead = new AtomicLong();

    @Inject
    Clock clock;

    @ConfigProperty(name = "siard.archives.max-per-scope", defaultValue = "5")
    int maxPerScope;

    @ConfigProperty(name = "siard.archives.max-total", defaultValue = "50")
    int maxTotal;

    @ConfigProperty(name = "siard.archives.idle-timeout", defaultValue = "PT30M")
    Duration idleTimeout;

    @ConfigProperty(name = "siard.archives.lock-timeout", defaultValue = "PT30S")
    Duration lockTimeout;

    /**
     * Opens {@code file} read-only for {@code scope}. On any failure {@code onRelease} has run once
     * and nothing is retained.
     *
     * @throws ProblemException 429 if a limit is reached
     * @throws IOException      if the file is not a readable SIARD archive
     */
    public ArchiveId register(ArchiveScope scope, Path file, Runnable onRelease) throws IOException {
        reserve(scope, onRelease);
        Archive archive;
        try {
            archive = ArchiveImpl.newInstance();
            archive.openReadOnly(file.toFile());
        } catch (IOException | RuntimeException e) {
            unreserve(scope);
            onRelease.run();
            throw e instanceof IOException io ? io : new IOException("Archive cannot be opened", e);
        }
        ArchiveId id = ArchiveId.random();
        synchronized (this) {
            entries.put(id, new Entry(id, scope, archive, onRelease, clock.instant(), recordsRead));
            unreserve(scope);
        }
        return id;
    }

    private synchronized void reserve(ArchiveScope scope, Runnable onRelease) {
        long inScope = entries.values().stream().filter(e -> e.scope.equals(scope)).count()
                + pending.getOrDefault(scope, 0);
        long total = entries.size() + pending.values().stream().mapToInt(Integer::intValue).sum();
        if (inScope >= maxPerScope || total >= maxTotal) {
            onRelease.run();
            throw new ProblemException(429, "Too many open archives");
        }
        pending.merge(scope, 1, Integer::sum);
    }

    private synchronized void unreserve(ArchiveScope scope) {
        pending.computeIfPresent(scope, (s, n) -> n > 1 ? n - 1 : null);
    }

    /**
     * Takes the archive's lock for one request. A foreign, unknown or closed id gives the same 404.
     *
     * @throws ProblemException 404 if there is no such archive in the scope, 503 if the lock
     *                          cannot be taken within the lock timeout
     */
    public ArchiveLease acquire(ArchiveScope scope, ArchiveId id) {
        Entry entry = find(scope, id);
        lock(entry);
        if (entries.get(id) != entry) {
            entry.lock.release();
            throw notFound();
        }
        entry.lastUsed = clock.instant();
        return new ArchiveLease(entry);
    }

    /**
     * Closes the archive and runs its {@code onRelease}.
     *
     * @throws ProblemException 404 if there is no such archive in the scope
     */
    public void close(ArchiveScope scope, ArchiveId id) {
        Entry entry = find(scope, id);
        lock(entry);
        try {
            if (!entries.remove(id, entry)) {
                throw notFound();
            }
            release(entry);
        } finally {
            entry.lock.release();
        }
    }

    /** Closes every archive that has not been used for the idle timeout; an archive in use is skipped. */
    @Scheduled(every = "1m")
    public void evictIdle() {
        Instant cutoff = clock.instant().minus(idleTimeout);
        for (Entry entry : List.copyOf(entries.values())) {
            if (entry.lastUsed.isBefore(cutoff) && entry.lock.tryAcquire()) {
                try {
                    if (entry.lastUsed.isBefore(cutoff) && entries.remove(entry.id, entry)) {
                        release(entry);
                    }
                } finally {
                    entry.lock.release();
                }
            }
        }
    }

    void onShutdown(@Observes ShutdownEvent event) {
        closeAll();
    }

    /**
     * Closes every open archive. Waits at most {@code lock-timeout} per archive for a running
     * request; an archive still locked after that is closed anyway (design D3), so a lock that was
     * never released cannot keep shutdown from completing.
     */
    public void closeAll() {
        for (Entry entry : List.copyOf(entries.values())) {
            boolean locked = false;
            try {
                locked = entry.lock.tryAcquire(lockTimeout.toMillis(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if (!locked) {
                LOG.warn("Archive {} is still in use after {}; closing it anyway", entry.id, lockTimeout);
            }
            try {
                if (entries.remove(entry.id, entry)) {
                    release(entry);
                }
            } finally {
                if (locked) {
                    entry.lock.release();
                }
            }
        }
    }

    /** When the archive was last used, if it is open in the scope. */
    public Optional<Instant> lastUsed(ArchiveScope scope, ArchiveId id) {
        Entry entry = entries.get(id);
        return entry != null && entry.scope.equals(scope) ? Optional.of(entry.lastUsed) : Optional.empty();
    }

    /** Records read from archive files so far, over all archives (paging and search; design D4). */
    public long recordsRead() {
        return recordsRead.get();
    }

    /** Number of open archives over all scopes. */
    public int size() {
        return entries.size();
    }

    private Entry find(ArchiveScope scope, ArchiveId id) {
        Entry entry = entries.get(id);
        if (entry == null || !entry.scope.equals(scope)) {
            throw notFound();
        }
        return entry;
    }

    private void lock(Entry entry) {
        try {
            if (!entry.lock.tryAcquire(lockTimeout.toMillis(), TimeUnit.MILLISECONDS)) {
                throw new ProblemException(503, "The archive is busy, try again later");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ProblemException(503, "The archive is busy, try again later");
        }
    }

    private static void release(Entry entry) {
        // callers only get here after winning entries.remove(id, entry), so this runs once per entry
        try {
            entry.cursors.closeAll();
            entry.archive.close();
        } catch (IOException | RuntimeException e) {
            LOG.warn("Archive {} could not be closed cleanly", entry.id, e);
        } finally {
            entry.onRelease.run();
        }
    }

    /** Unknown, foreign and closed ids all answer the same 404. */
    static ProblemException notFound() {
        return new ProblemException(404, null);
    }
}
