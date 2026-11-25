package ch.admin.bar.siardsuite.localwebapp.files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ch.admin.bar.siardsuite.webcore.error.ProblemException;

/**
 * A read error while iterating a folder (e.g. a share that drops mid-listing) maps to the same
 * status as one when opening it, not to a 500.
 */
class FolderListerTest {

    @TempDir
    Path dir;

    @Test
    void errorDuringIterationIsUnwrapped() {
        FolderLister lister = failingDuringIteration(new AccessDeniedException("x"));

        assertThrows(AccessDeniedException.class, () -> lister.list(dir, filter()));
    }

    @Test
    void errorDuringIterationMapsToItsStatus() {
        assertEquals(403, statusFor(new AccessDeniedException("x")));
        assertEquals(404, statusFor(new NoSuchFileException("x")));
    }

    private int statusFor(IOException cause) {
        FolderListings listings = new FolderListings(failingDuringIteration(cause)::list, 1, Duration.ofSeconds(5));
        return assertThrows(ProblemException.class, () -> listings.list(dir, filter())).status();
    }

    private static FolderLister failingDuringIteration(IOException cause) {
        return new FolderLister() {
            @Override
            DirectoryStream<Path> open(Path folder) {
                return new DirectoryStream<>() {
                    @Override
                    public Iterator<Path> iterator() {
                        return new Iterator<>() {
                            @Override
                            public boolean hasNext() {
                                throw new DirectoryIteratorException(cause);
                            }

                            @Override
                            public Path next() {
                                throw new DirectoryIteratorException(cause);
                            }
                        };
                    }

                    @Override
                    public void close() {
                        // nothing opened
                    }
                };
            }
        };
    }

    private static ListingFilter filter() {
        return new ListingFilter(List.of(), false, true);
    }
}
