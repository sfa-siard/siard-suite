package ch.admin.bar.siardsuite.webcore.archive;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.fixture;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.get;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.newScope;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.tablePath;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spec requirement "Bounded search in a table", the scenarios with a row budget of 5000, on
 * {@code payment} (16049 rows) of {@code sfdbsakila.siard}.
 */
@QuarkusTest
@TestProfile(SmallSearchBudgetProfile.class)
class SearchBudgetTest {

    @Inject
    ArchiveRegistry registry;

    private ArchiveScope scope;
    private String archive;
    private String payment;

    @BeforeEach
    void openPayment() throws IOException {
        scope = newScope();
        archive = "/api/archives/" + registry.register(scope, fixture("sfdbsakila.siard"), () -> { }).value();
        payment = archive + tablePath("sfdbsakila.siard", "payment");
    }

    @AfterEach
    void closeAll() {
        registry.closeAll();
    }

    @Test
    void scanBudgetIsExhausted() {
        get(scope, payment + "/search?q=zzz&afterRow=-1").then().statusCode(200)
                .body("found", equalTo(false))
                .body("complete", equalTo(false))
                .body("nextRow", equalTo(5000));
        // the scan holds the lock only during a request: the archive answers in between
        get(scope, archive).then().statusCode(200);

        long before = registry.recordsRead();
        get(scope, payment + "/search?q=zzz&afterRow=4999").then().statusCode(200)
                .body("nextRow", equalTo(10000));
        long read = registry.recordsRead() - before;
        assertTrue(read <= 5000, "continuation read " + read + " records, so it did not continue at row 5000");
    }

    @Test
    void searchDoesNotDisturbPaging() {
        for (int offset = 0; offset <= 1900; offset += 100) {
            get(scope, payment + "/rows?offset=" + offset + "&limit=100").then().statusCode(200);
        }

        long before = registry.recordsRead();
        get(scope, payment + "/search?q=zzz&afterRow=-1").then().statusCode(200);
        get(scope, payment + "/search?q=zzz&afterRow=4999").then().statusCode(200);
        long searchRead = registry.recordsRead() - before;
        before = registry.recordsRead();
        get(scope, payment + "/rows?offset=2000&limit=100").then().statusCode(200);
        long pageRead = registry.recordsRead() - before;

        assertTrue(searchRead <= 10000, "search read " + searchRead);
        assertTrue(pageRead <= 100, "page read " + pageRead);
    }
}
