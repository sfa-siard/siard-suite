package ch.admin.bar.siardsuite.localwebapp.web;

import ch.admin.bar.siardsuite.localwebapp.support.Sessions;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

/**
 * Spec requirement "API access only from the agent's own pages". Every request carries a valid
 * session cookie, as the browser would attach it, so only the Sec-Fetch-Site header decides.
 */
@QuarkusTest
class FetchMetadataTest {

    @Test
    void requestFromAPageOnAnotherLocalPort() {
        String cookie = Sessions.cookieValue();
        for (String method : new String[] {"GET", "POST"}) {
            withSession(cookie, "same-site")
                    .request(method, "/api/system/info")
                    .then().statusCode(403)
                    .contentType(startsWith("application/problem+json"))
                    .body(not(containsString("appVersion")));
        }
        // the event stream too: otherwise such a page could keep the agent from exiting
        withSession(cookie, "same-site")
                .get("/api/events")
                .then().statusCode(403)
                .contentType(startsWith("application/problem+json"));
    }

    @Test
    void requestFromAForeignSite() {
        withSession(Sessions.cookieValue(), "cross-site")
                .get("/api/system/info")
                .then().statusCode(403);
    }

    @Test
    void requestsFromOwnPagesAndNonBrowserClients() {
        String cookie = Sessions.cookieValue();
        withSession(cookie, "same-origin").get("/api/system/info").then().statusCode(200);
        withSession(cookie, "none").get("/api/system/info").then().statusCode(200);
        given().cookie(Sessions.cookieName(), cookie).get("/api/system/info").then().statusCode(200);
    }

    private static RequestSpecification withSession(String cookie, String secFetchSite) {
        return given()
                .cookie(Sessions.cookieName(), cookie)
                .header(RequestFilters.SEC_FETCH_SITE, secFetchSite);
    }
}
