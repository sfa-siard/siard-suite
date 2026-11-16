package ch.admin.bar.siardsuite.localwebapp.agent;

import ch.admin.bar.siardsuite.localwebapp.support.MutableClock;
import ch.admin.bar.siardsuite.localwebapp.support.RecordingShutdown;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spec scenario "No window connects after launch". A profile of its own gives a fresh agent
 * that no other test has opened a stream on.
 */
@QuarkusTest
@TestProfile(NoWindowExitTest.FreshAgent.class)
class NoWindowExitTest {

    public static class FreshAgent extends IdleExitTest.ShortGrace {
    }

    @Inject
    MutableClock clock;

    @Inject
    RecordingShutdown shutdown;

    @Test
    void noWindowConnectsAfterLaunch() throws Exception {
        assertFalse(shutdown.awaitExit(Duration.ofSeconds(2)), "agent exited while the launch link was still valid");
        clock.advance(LaunchCredentials.TOKEN_LIFETIME.plus(IdleExitTest.GRACE).plusSeconds(1));
        assertTrue(shutdown.awaitExit(Duration.ofSeconds(5)), "agent did not exit after the launch link expired");
    }
}
