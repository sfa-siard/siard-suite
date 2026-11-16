package ch.admin.bar.siardsuite.localwebapp.web;

import ch.admin.bar.siardsuite.localwebapp.support.Sessions;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Spec requirement "Frontend delivery".
 */
@QuarkusTest
class FrontendDeliveryTest {

    // the mount point in the frontend's index.html
    private static final String INDEX_MARKER = "<div id=\"root\">";

    @Test
    void deepLinkReload() {
        given().get("/about")
                .then().statusCode(200)
                .contentType(startsWith("text/html"))
                .body(containsString(INDEX_MARKER));
    }

    @Test
    void unknownApiPath() {
        given().cookie(Sessions.cookieName(), Sessions.cookieValue())
                .get("/api/does-not-exist")
                .then().statusCode(404)
                .contentType(startsWith("application/problem+json"));
    }

    @Test
    void missingStaticFile() {
        given().get("/assets/does-not-exist.js")
                .then().statusCode(404)
                .body(not(containsString(INDEX_MARKER)));
    }

    @Test
    void reservedPathsDoNotFallBack() {
        for (String path : new String[] {"/q/does-not-exist", "/launch/extra"}) {
            given().get(path)
                    .then().statusCode(404)
                    .body(not(containsString(INDEX_MARKER)));
        }
    }

    // raw requests: HTTP clients would normalise the dot segments before sending them
    @Test
    void pathTraversal() throws IOException {
        for (String path : new String[] {"/../../etc/passwd", "/%2e%2e/%2e%2e/etc/passwd"}) {
            String response = rawGet(path);
            assertFalse(response.contains("root:"), path + " returned a file outside the frontend:\n" + response);
        }
    }

    private static String rawGet(String path) throws IOException {
        int port = Sessions.port();
        try (Socket socket = new Socket("127.0.0.1", port)) {
            String request = "GET " + path + " HTTP/1.1\r\nHost: 127.0.0.1:" + port + "\r\nConnection: close\r\n\r\n";
            socket.getOutputStream().write(request.getBytes(StandardCharsets.US_ASCII));
            try (InputStream in = socket.getInputStream()) {
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
    }
}
