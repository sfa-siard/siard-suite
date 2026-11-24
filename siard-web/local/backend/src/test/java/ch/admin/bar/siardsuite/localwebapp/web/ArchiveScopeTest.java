package ch.admin.bar.siardsuite.localwebapp.web;

import ch.admin.bar.siardsuite.localwebapp.support.Sessions;
import ch.admin.bar.siardsuite.webcore.archive.ArchiveId;
import ch.admin.bar.siardsuite.webcore.archive.ArchiveRegistry;
import ch.admin.bar.siardsuite.webcore.archive.ArchiveScope;
import ch.admin.bar.siardsuite.webcore.error.ProblemDetail;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Spec requirement "Archive scope of the local agent"; the fixture is registered through the
 * agent's archive registry, read in place from siard-api's test resources.
 */
@QuarkusTest
class ArchiveScopeTest {

    @Inject
    ArchiveRegistry registry;

    @Inject
    LocalScopeResolver scopes;

    @AfterEach
    void closeAll() {
        registry.closeAll();
    }

    private ArchiveId registerSample(ArchiveScope scope) throws IOException {
        return registry.register(scope, Path.of(System.getProperty("siard.testfiles"), "sample.siard"), () -> { });
    }

    @Test
    void registeredArchiveIsReachableWithTheSession() throws IOException {
        ArchiveId id = registerSample(scopes.scope());

        given().cookie(Sessions.cookieName(), Sessions.cookieValue())
                .get("/api/archives/" + id.value())
                .then().statusCode(200);
    }

    @Test
    void archiveApiWithoutASession() throws IOException {
        ArchiveId id = registerSample(scopes.scope());
        Instant lastUsed = registry.lastUsed(scopes.scope(), id).orElseThrow();

        given().get("/api/archives/" + id.value()).then().statusCode(401);
        assertEquals(lastUsed, registry.lastUsed(scopes.scope(), id).orElseThrow());
    }

    @Test
    void archiveOfAnotherAgentInstance() throws IOException {
        // stands in for an archive of another instance: same file, a scope that is not this agent's
        ArchiveId foreign = registerSample(new ArchiveScope("another-agent-instance"));
        String cookie = Sessions.cookieValue();

        Response response = given().cookie(Sessions.cookieName(), cookie).get("/api/archives/" + foreign.value());
        Response unissued = given().cookie(Sessions.cookieName(), cookie)
                .get("/api/archives/" + ArchiveId.random().value());

        response.then().statusCode(404).contentType(startsWith(ProblemDetail.MEDIA_TYPE));
        assertEquals(unissued.asString(), response.asString());
    }
}
