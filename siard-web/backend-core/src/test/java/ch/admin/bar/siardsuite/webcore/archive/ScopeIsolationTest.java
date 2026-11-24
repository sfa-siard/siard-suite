package ch.admin.bar.siardsuite.webcore.archive;

import ch.admin.bar.siardsuite.webcore.error.ProblemDetail;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.delete;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.fixture;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.get;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.newScope;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Spec requirement "Archives are isolated by scope".
 */
@QuarkusTest
class ScopeIsolationTest {

    @Inject
    ArchiveRegistry registry;

    @AfterEach
    void closeAll() {
        registry.closeAll();
    }

    private String register(ArchiveScope scope) throws IOException {
        return registry.register(scope, fixture("sample.siard"), () -> { }).value();
    }

    @Test
    void sameScopeReachesItsArchive() throws IOException {
        ArchiveScope a = newScope();
        String id = register(a);

        get(a, "/api/archives/" + id).then().statusCode(200);
    }

    @Test
    void anotherScopeCannotTellTheArchiveExists() throws IOException {
        String id = register(newScope());
        ArchiveScope b = newScope();

        Response foreign = get(b, "/api/archives/" + id);
        Response unissued = get(b, "/api/archives/" + ArchiveId.random().value());

        foreign.then().statusCode(404).contentType(startsWith(ProblemDetail.MEDIA_TYPE));
        unissued.then().statusCode(404).contentType(startsWith(ProblemDetail.MEDIA_TYPE));
        assertEquals(unissued.asString(), foreign.asString());
    }

    @Test
    void requestWithoutAScope() throws IOException {
        String id = register(newScope());

        given().get("/api/archives/" + id)
                .then()
                .statusCode(401)
                .body(not(containsString("dbName")));
    }

    @Test
    void aForeignScopeCannotCloseAnArchive() throws IOException {
        ArchiveScope a = newScope();
        String id = register(a);

        delete(newScope(), "/api/archives/" + id).then().statusCode(404);
        get(a, "/api/archives/" + id).then().statusCode(200);
    }
}
