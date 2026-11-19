package ch.admin.bar.siardsuite.webcore.archive;

import ch.admin.bar.siardsuite.webcore.error.ProblemDetail;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.fixture;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.get;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.newScope;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.tablePath;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spec requirement "Paged table rows", on {@code payment} (16049 rows) of {@code sfdbsakila.siard}.
 */
@QuarkusTest
class PagingTest {

    @Inject
    ArchiveRegistry registry;

    private ArchiveScope scope;
    private String rows;

    @BeforeEach
    void openPayment() throws IOException {
        scope = newScope();
        String id = registry.register(scope, fixture("sfdbsakila.siard"), () -> { }).value();
        rows = "/api/archives/" + id + tablePath("sfdbsakila.siard", "payment") + "/rows";
    }

    @AfterEach
    void closeAll() {
        registry.closeAll();
    }

    @Test
    void pageOfRows() {
        Response response = get(scope, rows + "?offset=200&limit=50");

        response.then().statusCode(200)
                .body("total", equalTo(16049))
                .body("offset", equalTo(200))
                .body("rows", hasSize(50));
        assertEquals(IntStream.range(200, 250).boxed().toList(), response.jsonPath().getList("rows.row", Integer.class));
    }

    @Test
    void lastPartialPageAndOffsetBeyondTheEnd() {
        get(scope, rows + "?offset=16040&limit=100").then().statusCode(200).body("rows", hasSize(9));
        get(scope, rows + "?offset=20000&limit=100").then().statusCode(200)
                .body("rows", empty())
                .body("total", equalTo(16049));
    }

    @Test
    void invalidPagingParameters() {
        for (String query : List.of("offset=-1", "limit=0", "limit=1001", "offset=abc")) {
            get(scope, rows + "?" + query).then()
                    .statusCode(400)
                    .contentType(startsWith(ProblemDetail.MEDIA_TYPE))
                    .body(not(containsString("\"cells\"")));
        }
    }

    @Test
    void forwardPagingDoesNotRescan() {
        get(scope, rows + "?offset=0&limit=100").then().statusCode(200);
        for (int offset = 100; offset <= 1900; offset += 100) {
            assertReadsAtMost100(offset);
        }
        assertReadsAtMost100(0);
    }

    private void assertReadsAtMost100(int offset) {
        long before = registry.recordsRead();
        get(scope, rows + "?offset=" + offset + "&limit=100").then().statusCode(200);
        long read = registry.recordsRead() - before;
        assertTrue(read <= 100, "offset " + offset + " read " + read + " records");
    }
}
