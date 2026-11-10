package ch.admin.bar.siardsuite.localwebapp.web;

import ch.admin.bar.siardsuite.localwebapp.support.Sessions;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

/**
 * Spec requirement "Loopback Host header required".
 */
@QuarkusTest
class HostCheckTest {

    @Test
    void correctHostIsAccepted() {
        given().header("Host", "127.0.0.1:" + Sessions.port())
                .get("/q/health")
                .then().statusCode(200);
    }

    @Test
    void launchLinkViaDifferentHostNameIsRejectedAndTokenStaysUsable() {
        int port = Sessions.port();
        String token = Sessions.newToken();

        given().redirects().follow(false)
                .header("Host", "localhost:" + port)
                .queryParam("token", token)
                .get("/launch")
                .then()
                .statusCode(403)
                .contentType(startsWith(ProblemDetail.MEDIA_TYPE))
                .body("detail", containsString("http://127.0.0.1:" + port + "/"))
                .header("Set-Cookie", nullValue());

        given().redirects().follow(false)
                .header("Host", "127.0.0.1:" + port)
                .queryParam("token", token)
                .get("/launch")
                .then()
                .statusCode(303)
                .header("Location", equalTo("/"));
    }

    @Test
    void foreignHostIsRejectedEverywhere() {
        int port = Sessions.port();
        String session = Sessions.cookieValue();
        for (String host : List.of("[::1]:" + port, "evil.example:" + port, "127.0.0.1")) {
            for (String path : List.of("/", "/api/system/info", "/q/health")) {
                given().header("Host", host)
                        .cookie(Sessions.cookieName(), session)
                        .get(path)
                        .then()
                        .statusCode(403)
                        .body(not(containsString("<!doctype html")))
                        .body(not(containsString("\"ok\"")))
                        .body(not(containsString("\"UP\"")));
            }
        }
    }
}
