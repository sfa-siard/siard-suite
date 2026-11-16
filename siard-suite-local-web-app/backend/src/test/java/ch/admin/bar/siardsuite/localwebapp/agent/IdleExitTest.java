package ch.admin.bar.siardsuite.localwebapp.agent;

import ch.admin.bar.siardsuite.localwebapp.support.EventStreamClient;
import ch.admin.bar.siardsuite.localwebapp.support.RecordingShutdown;
import ch.admin.bar.siardsuite.localwebapp.support.Sessions;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spec requirement "Exit when no window is connected", scenarios that do not need the packaged
 * app; "Last window closed" is in {@code PackagedAgentIT}. Each test opens a stream before
 * resetting the recorder, which also cancels an exit timer left over from a previous test.
 */
@QuarkusTest
@TestProfile(IdleExitTest.ShortGrace.class)
class IdleExitTest {

    static final Duration GRACE = Duration.ofSeconds(1);

    public static class ShortGrace implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("siard.agent.idle-exit-grace", GRACE.toMillis() + "ms");
        }
    }

    @Inject
    RecordingShutdown shutdown;

    @Test
    void reloadWithinGracePeriod() throws Exception {
        String cookie = Sessions.cookieValue();
        EventStreamClient first = open(cookie);
        shutdown.reset();
        first.close();
        try (EventStreamClient reloaded = open(cookie)) {
            assertFalse(shutdown.awaitExit(GRACE.multipliedBy(2)), "agent exited despite the reload");
        }
    }

    @Test
    void unauthorizedConnectionsDoNotKeepTheAgentAlive() throws Exception {
        EventStreamClient only = open(Sessions.cookieValue());
        shutdown.reset();
        only.close();
        long deadline = System.nanoTime() + GRACE.plusSeconds(5).toNanos();
        boolean exited = false;
        while (!exited && System.nanoTime() < deadline) {
            given().get("/api/events").then().statusCode(401);
            exited = shutdown.awaitExit(Duration.ofMillis(200));
        }
        assertTrue(exited, "agent did not exit within the grace period plus 5 seconds");
    }

    private static EventStreamClient open(String cookie) throws Exception {
        EventStreamClient stream = EventStreamClient.open(Sessions.port(), Sessions.cookieName(), cookie);
        // wait until the server has registered the stream
        assertTrue(stream.awaitLine(line -> line.startsWith("data:"), Duration.ofSeconds(2)).isPresent());
        return stream;
    }
}
