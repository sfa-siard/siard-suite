package ch.admin.bar.siardsuite.localwebapp.web;

import ch.admin.bar.siardsuite.localwebapp.support.Sessions;
import ch.admin.bar.siardsuite.webcore.error.ProblemDetail;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

/**
 * Spec requirement "Shared error format behind the session": the problem format comes from the
 * backend core's mapper, which is tested there; this proves the mapper is active in the local app.
 * The session cookie only lets the request pass the agent's session check.
 */
@QuarkusTest
class ErrorResponseTest {

    @Test
    void notFoundWithValidSession() {
        given().cookie(Sessions.cookieName(), Sessions.cookieValue())
                .get("/api/does-not-exist")
                .then()
                .statusCode(404)
                .contentType(startsWith(ProblemDetail.MEDIA_TYPE))
                .body("status", equalTo(404))
                .body("title", not(emptyOrNullString()));
    }
}
