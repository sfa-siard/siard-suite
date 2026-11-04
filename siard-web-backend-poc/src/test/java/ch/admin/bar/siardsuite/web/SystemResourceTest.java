package ch.admin.bar.siardsuite.web;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.awt.GraphicsEnvironment;

import static io.restassured.RestAssured.given;

@QuarkusTest
class SystemResourceTest {

    @Inject
    SessionToken sessionToken;

    @Test
    void shutdownWithValidTokenReturnsNoContent() {
        given()
                .queryParam("token", sessionToken.get())
                .when().post("/api/system/shutdown")
                .then().statusCode(204);
    }

    @Test
    void shutdownWithInvalidTokenReturnsUnauthorized() {
        given()
                .queryParam("token", "not-the-token")
                .when().post("/api/system/shutdown")
                .then().statusCode(401);
    }

    @Test
    void shutdownWithoutTokenReturnsUnauthorized() {
        given()
                .when().post("/api/system/shutdown")
                .then().statusCode(401);
    }

    @Test
    void filePickerHeadlessReturnsNoContent() {
        Assumptions.assumeTrue(GraphicsEnvironment.isHeadless());
        given()
                .queryParam("token", sessionToken.get())
                .when().post("/api/system/file-picker")
                .then().statusCode(204);
    }
}
