package ch.admin.bar.siardsuite.web;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
class SessionTokenFilterTest {

    @Inject
    SessionToken sessionToken;

    @Test
    void apiRequestWithoutTokenIsRejected() {
        given()
                .when().get("/api/archives/any-id/tree")
                .then().statusCode(401);
    }

    @Test
    void apiRequestWithInvalidTokenIsRejected() {
        given()
                .queryParam("token", "not-the-token")
                .when().get("/api/archives/any-id/tree")
                .then().statusCode(401);
    }

    @Test
    void apiRequestWithValidTokenPassesAuthentication() {
        given()
                .queryParam("token", sessionToken.get())
                .when().get("/api/archives/any-id/tree")
                .then().statusCode(404);
    }

    @Test
    void apiRequestWithBearerTokenPassesAuthentication() {
        given()
                .header("Authorization", "Bearer " + sessionToken.get())
                .when().get("/api/archives/any-id/tree")
                .then().statusCode(404);
    }

    @Test
    void healthEndpointIsReachableWithoutToken() {
        given()
                .when().get("/q/health")
                .then().statusCode(200);
    }

    @Test
    void staticFrontendIsReachableWithoutToken() {
        given()
                .when().get("/")
                .then().statusCode(200);
    }
}
