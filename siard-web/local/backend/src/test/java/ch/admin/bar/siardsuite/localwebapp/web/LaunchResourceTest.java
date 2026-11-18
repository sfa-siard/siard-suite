package ch.admin.bar.siardsuite.localwebapp.web;

import ch.admin.bar.siardsuite.localwebapp.support.MutableClock;
import ch.admin.bar.siardsuite.localwebapp.support.Sessions;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.Header;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Spec requirements "Session established by launch token" and "Rejected launch link".
 */
@QuarkusTest
class LaunchResourceTest {

    @Inject
    MutableClock clock;

    @Test
    void validTokenIsExchangedForSessionCookie() {
        String cookieName = Sessions.cookieName();
        given().redirects().follow(false)
                .queryParam("token", Sessions.newToken())
                .get("/launch")
                .then()
                .statusCode(303)
                .header("Location", equalTo("/"))
                .header("Set-Cookie", allOf(
                        containsString(cookieName + "="), containsString("HttpOnly"), containsString("SameSite=Strict")))
                .header("Cache-Control", equalTo("no-store"))
                .header("Referrer-Policy", equalTo("no-referrer"));
    }

    @Test
    void tokenExchangedJustBeforeExpiry() {
        String token = Sessions.newToken();
        clock.advance(Duration.ofSeconds(119));
        given().redirects().follow(false)
                .queryParam("token", token)
                .get("/launch")
                .then()
                .header("Set-Cookie", containsString(Sessions.cookieName() + "="));
    }

    @Test
    void secondExchangeOfSameTokenSetsNoCookie() {
        String token = Sessions.newToken();
        launch(token).then().statusCode(303).header("Location", "/");
        launch(token).then().header("Set-Cookie", nullValue());
    }

    @Test
    void expiredTokenIsRejected() {
        String token = Sessions.newToken();
        clock.advance(Duration.ofSeconds(121));
        launch(token).then()
                .statusCode(303)
                .header("Location", equalTo("/launch-failed"))
                .header("Set-Cookie", nullValue());
    }

    @Test
    void reusedUnknownOrMissingTokenIsRejected() {
        String used = Sessions.newToken();
        launch(used);
        for (Response response : List.of(launch(used), launch("never-issued"), launchWithoutToken())) {
            response.then()
                    .statusCode(303)
                    .header("Location", equalTo("/launch-failed"))
                    .header("Set-Cookie", nullValue());
        }
    }

    @Test
    void rejectionRevealsNothingAboutTheToken() {
        String expired = Sessions.newToken();
        clock.advance(Duration.ofMinutes(3));
        String used = Sessions.newToken();
        launch(used);

        Response expiredResponse = launch(expired);
        Response usedResponse = launch(used);
        Response unknownResponse = launch("never-issued");

        for (Response response : List.of(usedResponse, unknownResponse)) {
            assertEquals(expiredResponse.statusCode(), response.statusCode());
            assertEquals(headersWithoutDate(expiredResponse), headersWithoutDate(response));
            assertEquals(expiredResponse.body().asString(), response.body().asString());
        }
        assertNull(java.net.URI.create(expiredResponse.header("Location")).getQuery());
    }

    private static Response launch(String token) {
        return given().redirects().follow(false).queryParam("token", token).get("/launch");
    }

    private static Response launchWithoutToken() {
        return given().redirects().follow(false).get("/launch");
    }

    // Date differs by the second between responses and is not about the token
    private static List<String> headersWithoutDate(Response response) {
        return response.headers().asList().stream()
                .filter(header -> !header.getName().equalsIgnoreCase("Date"))
                .map(Header::toString)
                .sorted()
                .toList();
    }
}
