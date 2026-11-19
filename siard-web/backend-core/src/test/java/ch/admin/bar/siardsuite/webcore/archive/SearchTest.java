package ch.admin.bar.siardsuite.webcore.archive;

import ch.admin.bar.siardsuite.webcore.error.ProblemDetail;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.fixture;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.get;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.newScope;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.tablePath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;

/**
 * Spec requirement "Bounded search in a table", with the default budget (the budget scenarios are
 * in {@link SearchBudgetTest}).
 */
@QuarkusTest
class SearchTest {

    @Inject
    ArchiveRegistry registry;

    @TempDir
    Path tmp;

    @AfterEach
    void closeAll() {
        registry.closeAll();
    }

    private ArchiveScope scope;

    private String customerSearch() throws IOException {
        scope = newScope();
        String id = registry.register(scope, fixture("sfdbsakila.siard"), () -> { }).value();
        return "/api/archives/" + id + tablePath("sfdbsakila.siard", "customer") + "/search";
    }

    @Test
    void matchFound() throws IOException {
        String search = customerSearch();

        get(scope, search + "?q=WILSON&afterRow=-1").then().statusCode(200)
                .body("found", equalTo(true))
                .body("row", equalTo(7))
                .body("column", equalTo(3));
    }

    @Test
    void continueAfterAMatchAndReachTheEnd() throws IOException {
        String search = customerSearch();
        get(scope, search + "?q=WILSON&afterRow=-1").then().body("row", equalTo(7));

        get(scope, search + "?q=WILSON&afterRow=7").then().statusCode(200)
                .body("found", equalTo(false))
                .body("complete", equalTo(true));
    }

    @Test
    void invalidSearchInput() throws IOException {
        String search = customerSearch();

        for (String query : new String[]{"?q=", "?q=" + "a".repeat(201), "?q=a&afterRow=-2"}) {
            get(scope, search + query).then()
                    .statusCode(400)
                    .contentType(startsWith(ProblemDetail.MEDIA_TYPE));
        }
    }

    @Test
    void searchCoversOnlyTheDisplayedText() throws IOException {
        scope = newScope();
        String id = registry.register(scope, CellValuesTest.largeTextArchive(tmp), () -> { }).value();
        String search = "/api/archives/" + id + "/schemas/0/tables/0/search";

        get(scope, search + "?q=alpha42&afterRow=-1").then().statusCode(200)
                .body("found", equalTo(true))
                .body("row", equalTo(0))
                .body("column", equalTo(2));
        get(scope, search + "?q=omega42&afterRow=-1").then().statusCode(200)
                .body("found", equalTo(false))
                .body("complete", equalTo(true));
    }

    @Test
    void searchHonoursCaseOption() throws IOException {
        String search = customerSearch();

        get(scope, search + "?q=smith&afterRow=-1&matchCase=true").then().statusCode(200)
                .body("found", equalTo(false));
        get(scope, search + "?q=smith&afterRow=-1&matchCase=false").then().statusCode(200)
                .body("found", equalTo(true));
    }
}
