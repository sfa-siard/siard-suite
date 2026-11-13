package ch.admin.bar.siardsuite.localwebapp.web;

import ch.admin.bar.siardsuite.localwebapp.support.ProbeResource;
import ch.admin.bar.siardsuite.localwebapp.support.Sessions;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.startsWith;

/**
 * Spec requirement "Error responses".
 */
@QuarkusTest
class ErrorResponseTest {

    @Test
    void notFound() {
        given().cookie(Sessions.cookieName(), Sessions.cookieValue())
                .get("/api/does-not-exist")
                .then()
                .statusCode(404)
                .contentType(startsWith(ProblemDetail.MEDIA_TYPE))
                .body("status", equalTo(404))
                .body("title", not(emptyOrNullString()));
    }

    @Test
    void unexpectedFailure() {
        given().cookie(Sessions.cookieName(), Sessions.cookieValue())
                .get("/api/test-probe/fail")
                .then()
                .statusCode(500)
                .contentType(startsWith(ProblemDetail.MEDIA_TYPE));
    }

    @Test
    void noInternalDetailsLeaked() {
        given().cookie(Sessions.cookieName(), Sessions.cookieValue())
                .get("/api/test-probe/fail")
                .then()
                .body(not(containsString(ProbeResource.INTERNAL_MESSAGE)))
                .body(not(containsString("Exception")))
                .body(not(containsString("at ch.admin")));
    }
}
