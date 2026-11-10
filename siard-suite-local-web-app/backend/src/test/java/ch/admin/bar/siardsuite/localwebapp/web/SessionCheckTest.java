package ch.admin.bar.siardsuite.localwebapp.web;

import ch.admin.bar.siardsuite.localwebapp.support.Sessions;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

/**
 * Spec requirement "API access requires session".
 */
@QuarkusTest
class SessionCheckTest {

    @Test
    void authorizedApiRequest() {
        given().cookie(Sessions.cookieName(), Sessions.cookieValue())
                .get("/api/system/info")
                .then().statusCode(200);
    }

    @Test
    void staticFrontendAndHealthWithoutSession() {
        given().get("/").then().statusCode(200);
        given().get("/q/health").then().statusCode(200);
    }

    @Test
    void missingOrInvalidSessionIsRejected() {
        given().get("/api/system/info").then().statusCode(401);
        given().cookie(Sessions.cookieName(), "not-the-secret")
                .get("/api/system/info")
                .then().statusCode(401);
    }
}
