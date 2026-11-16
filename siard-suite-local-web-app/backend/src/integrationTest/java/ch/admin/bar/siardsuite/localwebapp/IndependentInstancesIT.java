package ch.admin.bar.siardsuite.localwebapp;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spec requirement "Independent instances": two packaged agents side by side.
 */
class IndependentInstancesIT {

    private static final Duration GRACE = Duration.ofSeconds(2);

    @Test
    void twoLaunches() throws Exception {
        try (PackagedAgent first = PackagedAgent.start();
             PackagedAgent second = PackagedAgent.start()) {
            assertNotEquals(first.port(), second.port());
            assertEquals(200, first.get("/q/health").statusCode());
            assertEquals(200, second.get("/q/health").statusCode());
        }
    }

    @Test
    void closingOneInstancesWindow() throws Exception {
        String grace = "-Dsiard.agent.idle-exit-grace=" + GRACE.toMillis() + "ms";
        try (PackagedAgent first = PackagedAgent.start(grace);
             PackagedAgent second = PackagedAgent.start(grace);
             AutoCloseable secondWindow = second.openWindow(second.sessionCookie())) {
            first.openWindow(first.sessionCookie()).close();
            assertTrue(first.awaitExit(GRACE.plusSeconds(5)), "first agent still running");
            assertTrue(second.processHandle().isAlive(), "second agent exited");
            assertEquals(200, second.get("/q/health").statusCode());
        }
    }

    // a browser sends the cookies of both instances to both, since cookies are not port-specific
    @Test
    void sessionOfOneInstanceIsRejectedByAnother() throws Exception {
        try (PackagedAgent first = PackagedAgent.start();
             PackagedAgent second = PackagedAgent.start()) {
            String firstCookie = first.sessionCookie();
            String firstSecret = firstCookie.substring(firstCookie.indexOf('=') + 1);
            String cookies = firstCookie + "; siard_session_" + second.port() + "=" + firstSecret;
            assertEquals(401, second.get("/api/system/info", "Cookie", cookies).statusCode());
        }
    }
}
