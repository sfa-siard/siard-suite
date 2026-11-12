package ch.admin.bar.siardsuite.localwebapp.web;

import ch.admin.bar.siardsuite.localwebapp.agent.AgentPort;
import ch.admin.bar.siardsuite.localwebapp.agent.LaunchCredentials;
import io.vertx.core.http.Cookie;
import io.vertx.core.json.Json;
import io.vertx.core.net.HostAndPort;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

/**
 * Router-level checks that run before REST resources, static files and {@code /q/*}
 * (design D4): the exact loopback Host (HTTP/1.1 {@code Host} or HTTP/2 {@code :authority}) on
 * every request; on {@code /api/*} that the request comes from the agent's own pages (Fetch
 * Metadata), then the session cookie.
 */
@ApplicationScoped
public class RequestFilters {

    public static final String SESSION_COOKIE_PREFIX = "siard_session_";
    static final String SEC_FETCH_SITE = "Sec-Fetch-Site";

    @Inject
    AgentPort port;

    @Inject
    LaunchCredentials credentials;

    void register(@Observes Router router) {
        router.route().order(Integer.MIN_VALUE).handler(this::requireLoopbackHost);
        router.route("/api/*").order(Integer.MIN_VALUE + 1).handler(this::requireOwnPages);
        router.route("/api/*").order(Integer.MIN_VALUE + 2).handler(this::requireSession);
    }

    public static String sessionCookieName(int port) {
        return SESSION_COOKIE_PREFIX + port;
    }

    // authority() is the Host header in HTTP/1.1 and the :authority pseudo-header in HTTP/2
    private void requireLoopbackHost(RoutingContext context) {
        HostAndPort authority = context.request().authority();
        int expectedPort = port.get();
        if (authority != null && "127.0.0.1".equals(authority.host()) && authority.port() == expectedPort) {
            context.next();
            return;
        }
        reject(context, ProblemDetail.of(403, "Open SIARD Suite via http://127.0.0.1:" + expectedPort + "/"));
    }

    /*
     * Cookies are not scoped by port, and for an IP address every port is the same site, so the
     * SameSite=Strict session cookie also travels with requests from pages on other
     * 127.0.0.1 ports (another local server, or another user's on a shared terminal server).
     * Browsers label each request with Sec-Fetch-Site: the agent's own pages send "same-origin",
     * a page on another port "same-site", a foreign site "cross-site"; "none" is a navigation the
     * user started. Requests without the header come from non-browser clients, which cannot carry
     * the user's cookie.
     */
    private void requireOwnPages(RoutingContext context) {
        String site = context.request().getHeader(SEC_FETCH_SITE);
        if (site == null || "same-origin".equals(site) || "none".equals(site)) {
            context.next();
            return;
        }
        reject(context, ProblemDetail.of(403, null));
    }

    private void requireSession(RoutingContext context) {
        Cookie cookie = context.request().getCookie(sessionCookieName(port.get()));
        if (cookie != null && credentials.isSessionSecret(cookie.getValue())) {
            context.next();
            return;
        }
        reject(context, ProblemDetail.of(401, null));
    }

    private static void reject(RoutingContext context, ProblemDetail problem) {
        context.response()
                .setStatusCode(problem.status())
                .putHeader("Content-Type", ProblemDetail.MEDIA_TYPE)
                .end(Json.encode(problem));
    }
}
