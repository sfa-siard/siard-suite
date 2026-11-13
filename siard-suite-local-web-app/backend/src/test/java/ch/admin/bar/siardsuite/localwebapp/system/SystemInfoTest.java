package ch.admin.bar.siardsuite.localwebapp.system;

import ch.admin.bar.siardsuite.localwebapp.support.Sessions;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;

/**
 * Spec requirement "System information endpoint"; "Information of the packaged agent" is in
 * {@code SystemInfoIT}, since only the packaged app has the full driver chain and build version.
 */
@QuarkusTest
class SystemInfoTest {

    @Test
    void unsupportedMethod() {
        given().cookie(Sessions.cookieName(), Sessions.cookieValue())
                .post("/api/system/info")
                .then().statusCode(405)
                .contentType(startsWith("application/problem+json"));
    }

    @Test
    void requestWithoutSession() {
        given().get("/api/system/info")
                .then().statusCode(401)
                .body(not(containsString("appVersion")));
    }
}
