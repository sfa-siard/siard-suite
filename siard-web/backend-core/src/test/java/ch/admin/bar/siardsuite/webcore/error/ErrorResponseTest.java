package ch.admin.bar.siardsuite.webcore.error;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

/**
 * Spec requirement "Shared error format", in a test application with only the core and no local
 * module; no request carries a cookie.
 */
@QuarkusTest
class ErrorResponseTest {

    @Test
    void notFoundWithoutAnySession() {
        given().get("/api/does-not-exist")
                .then()
                .statusCode(404)
                .contentType(startsWith(ProblemDetail.MEDIA_TYPE))
                .body("status", equalTo(404))
                .body("title", not(emptyOrNullString()));
    }

    @Test
    void unexpectedFailure() {
        given().get("/api/test-probe/fail")
                .then()
                .statusCode(500)
                .contentType(startsWith(ProblemDetail.MEDIA_TYPE));
    }

    @Test
    void noInternalDetailsLeaked() {
        given().get("/api/test-probe/fail")
                .then()
                .body(not(containsString(ProbeResource.INTERNAL_MESSAGE)))
                .body(not(containsString("Exception")))
                .body(not(containsString("at ch.admin")));
    }
}
