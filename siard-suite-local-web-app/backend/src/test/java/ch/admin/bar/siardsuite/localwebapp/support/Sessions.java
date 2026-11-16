package ch.admin.bar.siardsuite.localwebapp.support;

import ch.admin.bar.siardsuite.localwebapp.agent.AgentPort;
import ch.admin.bar.siardsuite.localwebapp.agent.LaunchCredentials;
import ch.admin.bar.siardsuite.localwebapp.web.RequestFilters;
import io.quarkus.arc.Arc;

import static io.restassured.RestAssured.given;

/**
 * Obtains a session cookie the way a browser does: through the launch endpoint.
 */
public final class Sessions {

    private Sessions() {
    }

    public static int port() {
        return Arc.container().instance(AgentPort.class).get().get();
    }

    public static String cookieName() {
        return RequestFilters.sessionCookieName(port());
    }

    public static String newToken() {
        return Arc.container().instance(LaunchCredentials.class).get().issueToken();
    }

    /** Returns the value of a fresh session cookie. */
    public static String cookieValue() {
        return given().redirects().follow(false)
                .queryParam("token", newToken())
                .get("/launch")
                .then().statusCode(303)
                .extract().cookie(cookieName());
    }
}
