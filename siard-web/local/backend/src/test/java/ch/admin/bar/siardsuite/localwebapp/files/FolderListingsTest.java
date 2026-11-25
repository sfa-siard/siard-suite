package ch.admin.bar.siardsuite.localwebapp.files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import ch.admin.bar.siardsuite.webcore.error.ProblemException;

/**
 * Time limit and bounded workers of folder listings (spec "Folder listing", scenario "Invalid or
 * unusable folder path"; design Risks): a stalled listing answers 504, a full pool 503 at once.
 */
class FolderListingsTest {

    private static final Path FOLDER = Path.of("/data").toAbsolutePath();
    private static final Duration LIMIT = Duration.ofMillis(150);
    private static final FolderListing EMPTY = new FolderListing(FOLDER.toString(), null, List.of(), false);

    private final CountDownLatch release = new CountDownLatch(1);

    // a lister stuck like a dead network share until the test releases it
    private final FolderListings.Lister stuck = (folder, filter) -> {
        try {
            release.await();
        } catch (InterruptedException e) {
            // like a blocked OS call: an interrupt does not end the wait
            awaitUninterruptibly(release);
        }
        return EMPTY;
    };

    @AfterEach
    void releaseWorkers() {
        release.countDown();
    }

    @Test
    void stalledListingAnswers504AfterTheLimit() {
        FolderListings listings = new FolderListings(stuck, 2, LIMIT);

        long start = System.nanoTime();
        ProblemException problem = assertThrows(ProblemException.class, () -> listings.list(FOLDER, all()));

        assertEquals(504, problem.status());
        assertTrue(elapsedMillis(start) >= LIMIT.toMillis());
    }

    @Test
    void fullPoolAnswers503AtOnce() {
        FolderListings listings = new FolderListings(stuck, FolderListings.WORKERS, LIMIT);
        for (int i = 0; i < FolderListings.WORKERS; i++) {
            assertEquals(504, assertThrows(ProblemException.class, () -> listings.list(FOLDER, all())).status());
        }

        long start = System.nanoTime();
        ProblemException problem = assertThrows(ProblemException.class, () -> listings.list(FOLDER, all()));

        assertEquals(503, problem.status());
        assertTrue(elapsedMillis(start) < LIMIT.toMillis(), "503 must not wait for the time limit");
    }

    @Test
    void workerIsReusableOnceTheCallReturns() throws InterruptedException {
        FolderListings listings = new FolderListings(stuck, 1, LIMIT);
        assertEquals(504, assertThrows(ProblemException.class, () -> listings.list(FOLDER, all())).status());
        assertEquals(503, assertThrows(ProblemException.class, () -> listings.list(FOLDER, all())).status());

        release.countDown();

        FolderListing listing = null;
        for (int attempt = 0; attempt < 50 && listing == null; attempt++) {
            try {
                listing = listings.list(FOLDER, all());
            } catch (ProblemException e) {
                assertEquals(503, e.status());
                TimeUnit.MILLISECONDS.sleep(20);
            }
        }
        assertEquals(EMPTY, listing);
    }

    @Test
    void fileSystemErrorsMapToStatusCodes() {
        assertEquals(404, statusFor(new NoSuchFileException("x")));
        assertEquals(400, statusFor(new NotDirectoryException("x")));
        assertEquals(403, statusFor(new AccessDeniedException("x")));
    }

    @Test
    void problemDetailsCarryNoExceptionMessage() {
        FolderListings listings = new FolderListings(
                (folder, filter) -> {
                    throw new AccessDeniedException("/secret/internal/name");
                }, 1, LIMIT);

        ProblemException problem = assertThrows(ProblemException.class, () -> listings.list(FOLDER, all()));

        assertEquals(FolderListings.NOT_READABLE, problem.detail());
    }

    private static int statusFor(IOException exception) {
        FolderListings listings = new FolderListings((folder, filter) -> {
            throw exception;
        }, 1, LIMIT);
        return assertThrows(ProblemException.class, () -> listings.list(FOLDER, all())).status();
    }

    private static ListingFilter all() {
        return new ListingFilter(List.of(), false, true);
    }

    private static long elapsedMillis(long startNanos) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
    }

    private static void awaitUninterruptibly(CountDownLatch latch) {
        boolean interrupted = false;
        while (true) {
            try {
                latch.await();
                break;
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }
}
