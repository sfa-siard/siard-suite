package ch.admin.bar.siardsuite.webcore.archive;

import ch.admin.bar.siardsuite.webcore.error.ProblemDetail;
import ch.admin.bar.siardsuite.webcore.testing.MutableClock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.fixture;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.get;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.newScope;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Spec requirement "Idle archives are closed", with the default idle timeout of 30 minutes; the
 * tests run the eviction directly instead of waiting for the scheduler.
 */
@QuarkusTest
class IdleEvictionTest {

    @Inject
    ArchiveRegistry registry;

    @Inject
    MutableClock clock;

    @AfterEach
    void closeAll() {
        registry.closeAll();
    }

    @Test
    void activityKeepsTheArchiveOpen() throws IOException {
        ArchiveScope scope = newScope();
        AtomicInteger releases = new AtomicInteger();
        String id = registry.register(scope, fixture("sample.siard"), releases::incrementAndGet).value();

        for (int i = 0; i < 3; i++) {
            clock.advance(Duration.ofMinutes(20));
            get(scope, "/api/archives/" + id).then().statusCode(200);
            registry.evictIdle();
        }
        assertEquals(0, releases.get());
    }

    @Test
    void idleArchiveIsReleased() throws IOException {
        ArchiveScope scope = newScope();
        AtomicInteger releases = new AtomicInteger();
        String id = registry.register(scope, fixture("sample.siard"), releases::incrementAndGet).value();

        clock.advance(Duration.ofMinutes(31));
        registry.evictIdle();

        assertEquals(1, releases.get());
        Response evicted = get(scope, "/api/archives/" + id);
        evicted.then().statusCode(404).contentType(startsWith(ProblemDetail.MEDIA_TYPE));
        assertEquals(get(scope, "/api/archives/" + ArchiveId.random().value()).asString(), evicted.asString());
    }

    @Test
    void releasedArchiveLeavesNoReadableData() throws IOException {
        ArchiveScope scope = newScope();
        String id = registry.register(scope, fixture("sample.siard"), () -> { }).value();
        String[] paths = {"/schemas/0/tables/0/rows?offset=0&limit=10", "/schemas/0/tables/0/rows/0/columns/2/content"};
        // the paths are right: they answer while the archive is open
        for (String path : paths) {
            get(scope, "/api/archives/" + id + path).then().statusCode(200);
        }
        clock.advance(Duration.ofMinutes(31));
        registry.evictIdle();

        for (String path : paths) {
            Response response = get(scope, "/api/archives/" + id + path);
            response.then().statusCode(404);
            assertFalse(response.asString().contains("cells"), path);
        }
    }
}
