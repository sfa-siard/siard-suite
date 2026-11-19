package ch.admin.bar.siardsuite.webcore.archive;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.fixture;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.get;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.newScope;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.tablePath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spec requirement "Concurrent requests on one archive are safe".
 */
@QuarkusTest
class ConcurrencyTest {

    @Inject
    ArchiveRegistry registry;

    @AfterEach
    void closeAll() {
        registry.closeAll();
    }

    @Test
    void concurrentRowRequestsAnswerAsIfIssuedAlone() throws Exception {
        ArchiveScope scope = newScope();
        String id = registry.register(scope, fixture("sfdbsakila.siard"), () -> { }).value();
        String rows = "/api/archives/" + id + tablePath("sfdbsakila.siard", "payment") + "/rows";
        List<String> requests = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            requests.add(rows + "?offset=" + (i * 2000) + "&limit=50");
        }
        List<String> sequential = new ArrayList<>();
        for (String request : requests) {
            sequential.add(get(scope, request).then().statusCode(200).extract().asString());
        }

        ExecutorService pool = Executors.newFixedThreadPool(8);
        try {
            CountDownLatch start = new CountDownLatch(1);
            List<Future<Response>> concurrent = new ArrayList<>();
            for (String request : requests) {
                concurrent.add(pool.submit(() -> {
                    start.await();
                    return get(scope, request);
                }));
            }
            start.countDown();
            for (int i = 0; i < requests.size(); i++) {
                Response response = concurrent.get(i).get();
                assertEquals(200, response.statusCode(), requests.get(i));
                assertEquals(sequential.get(i), response.asString(), requests.get(i));
            }
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void aSlowRequestDoesNotBlockAnotherOpenArchive() throws IOException {
        ArchiveScope scope = newScope();
        ArchiveId x = registry.register(scope, fixture("sample.siard"), () -> { });
        ArchiveId y = registry.register(scope, fixture("sample.siard"), () -> { });

        // a request in flight on X holds X's lock for as long as it runs
        try (ArchiveLease slowRequestOnX = registry.acquire(scope, x)) {
            long start = System.nanoTime();
            get(scope, "/api/archives/" + y.value()).then().statusCode(200);
            Duration took = Duration.ofNanos(System.nanoTime() - start);
            assertTrue(took.compareTo(Duration.ofSeconds(5)) < 0, "took " + took);
        }
    }
}
