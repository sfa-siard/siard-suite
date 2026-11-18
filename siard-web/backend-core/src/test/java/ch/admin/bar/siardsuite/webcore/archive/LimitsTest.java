package ch.admin.bar.siardsuite.webcore.archive;

import ch.admin.bar.siardsuite.webcore.error.ProblemDetail;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.fixture;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.get;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.newScope;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Spec requirement "Open archives are limited per scope", with the default limits.
 */
@QuarkusTest
class LimitsTest {

    @Inject
    ArchiveRegistry registry;

    @AfterEach
    void closeAll() {
        registry.closeAll();
    }

    private List<String> registerMany(ArchiveScope scope, int count) throws IOException {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ids.add(registry.register(scope, fixture("sample.siard"), () -> { }).value());
        }
        return ids;
    }

    @Test
    void registrationUpToTheLimit() throws IOException {
        ArchiveScope scope = newScope();
        registerMany(scope, 4);

        String fifth = registry.register(scope, fixture("sample.siard"), () -> { }).value();
        get(scope, "/api/archives/" + fifth).then().statusCode(200);
    }

    @Test
    void registrationBeyondThePerScopeLimit() throws IOException {
        ArchiveScope scope = newScope();
        List<String> five = registerMany(scope, 5);

        given().header(TestScopeResolver.HEADER, scope.value())
                .queryParam("fixture", "sample.siard")
                .queryParam("tag", "sixth-" + scope.value())
                .post("/api/test-archives")
                .then()
                .statusCode(429)
                .contentType(startsWith(ProblemDetail.MEDIA_TYPE));
        for (String id : five) {
            get(scope, "/api/archives/" + id).then().statusCode(200);
        }
        assertEquals(1, TestRegistrationResource.RELEASES.get("sixth-" + scope.value()).get());
    }

    @Test
    void oneScopeCannotExhaustAnother() throws IOException {
        registerMany(newScope(), 5);
        ArchiveScope b = newScope();

        String id = registry.register(b, fixture("sample.siard"), () -> { }).value();
        get(b, "/api/archives/" + id).then().statusCode(200);
    }
}
