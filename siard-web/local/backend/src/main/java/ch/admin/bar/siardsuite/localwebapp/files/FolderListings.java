package ch.admin.bar.siardsuite.localwebapp.files;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;

import ch.admin.bar.siardsuite.webcore.error.ProblemException;

/**
 * Runs listings with a time limit on a bounded set of workers (design D2, D3 and Risks). Java
 * cannot cancel a blocked file-system call, so a listing past the limit is answered 504 and its
 * worker is abandoned until the call returns. At most {@code workers} listings run at once; when
 * all are busy a listing is answered 503 at once instead of starting another thread. Workers are
 * daemon threads, so a blocked one never keeps the agent from exiting.
 */
@ApplicationScoped
public class FolderListings {

    static final int WORKERS = 8;
    static final Duration TIME_LIMIT = Duration.ofSeconds(10);

    static final String NOT_FOUND = "Nothing exists at this path";
    static final String NOT_A_FOLDER = "The path is not a folder";
    static final String NOT_READABLE = "The folder may not be read";
    static final String NOT_RESPONDING = "The location is not responding";
    static final String ALL_BUSY = "Too many locations are not responding, try again later";

    /** A blocking listing, replaceable in tests. */
    interface Lister {
        FolderListing list(Path folder, ListingFilter filter) throws IOException;
    }

    private final Lister lister;
    private final Duration timeLimit;
    private final Semaphore free;
    private final ExecutorService executor = Executors.newCachedThreadPool(task -> {
        Thread thread = new Thread(task, "folder-listing");
        thread.setDaemon(true);
        return thread;
    });

    FolderListings() {
        this(new FolderLister()::list, WORKERS, TIME_LIMIT);
    }

    FolderListings(Lister lister, int workers, Duration timeLimit) {
        this.lister = lister;
        this.timeLimit = timeLimit;
        this.free = new Semaphore(workers);
    }

    /**
     * @throws ProblemException 404, 400 (not a folder), 403, 504 (time limit) or 503 (all busy)
     */
    public FolderListing list(Path folder, ListingFilter filter) {
        if (!free.tryAcquire()) {
            throw new ProblemException(503, ALL_BUSY);
        }
        Future<FolderListing> listing;
        try {
            // the permit is released only when the file-system call returns, not at the time limit
            listing = executor.submit(() -> {
                try {
                    return lister.list(folder, filter);
                } finally {
                    free.release();
                }
            });
        } catch (RuntimeException e) {
            free.release();
            throw e;
        }
        try {
            return listing.get(timeLimit.toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new ProblemException(504, NOT_RESPONDING);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ProblemException(503, ALL_BUSY);
        } catch (ExecutionException e) {
            throw problemFor(e.getCause());
        }
    }

    private static RuntimeException problemFor(Throwable cause) {
        return switch (cause) {
            case NoSuchFileException ignored -> new ProblemException(404, NOT_FOUND);
            case NotDirectoryException ignored -> new ProblemException(400, NOT_A_FOLDER);
            case AccessDeniedException ignored -> new ProblemException(403, NOT_READABLE);
            case IOException io -> new UncheckedIOException(io);
            case RuntimeException runtime -> runtime;
            default -> new IllegalStateException(cause);
        };
    }

    @PreDestroy
    void shutdown() {
        executor.shutdownNow();
    }
}
