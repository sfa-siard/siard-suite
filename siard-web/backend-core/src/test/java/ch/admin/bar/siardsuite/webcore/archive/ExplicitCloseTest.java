package ch.admin.bar.siardsuite.webcore.archive;

import ch.admin.bar.siardsuite.webcore.error.ProblemDetail;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.delete;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.fixture;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.newScope;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Spec requirement "Archives can be closed explicitly".
 */
@QuarkusTest
class ExplicitCloseTest {

    @Inject
    ArchiveRegistry registry;

    @AfterEach
    void closeAll() {
        registry.closeAll();
    }

    @Test
    void closeAnArchive() throws IOException {
        ArchiveScope scope = newScope();
        AtomicInteger releases = new AtomicInteger();
        String id = registry.register(scope, fixture("sample.siard"), releases::incrementAndGet).value();

        delete(scope, "/api/archives/" + id).then().statusCode(204);
        assertEquals(1, releases.get());
    }

    @Test
    void closeTwice() throws IOException {
        ArchiveScope scope = newScope();
        AtomicInteger releases = new AtomicInteger();
        String id = registry.register(scope, fixture("sample.siard"), releases::incrementAndGet).value();
        delete(scope, "/api/archives/" + id).then().statusCode(204);

        delete(scope, "/api/archives/" + id)
                .then()
                .statusCode(404)
                .contentType(startsWith(ProblemDetail.MEDIA_TYPE));
        assertEquals(1, releases.get());
    }
}
