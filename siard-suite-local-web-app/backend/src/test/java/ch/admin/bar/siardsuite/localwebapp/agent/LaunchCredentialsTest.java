package ch.admin.bar.siardsuite.localwebapp.agent;

import io.quarkus.runtime.LaunchMode;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spec scenario "Development mode uses fixed credentials". Running quarkusDev in a test is too
 * heavy, so the launch-mode rule is tested on {@link LaunchCredentials} directly; the other two
 * scenarios of the requirement run against the packaged app (PackagedAgentIT).
 */
class LaunchCredentialsTest {

    @Test
    void developmentModeUsesFixedReusableNonExpiringToken() {
        Instant start = Instant.parse("2026-01-01T00:00:00Z");
        LaunchCredentials credentials = new LaunchCredentials(LaunchMode.DEVELOPMENT,
                Optional.of("dev-token"), Optional.of("dev-secret"), Clock.fixed(start, ZoneOffset.UTC));
        LaunchCredentials threeMinutesLater = new LaunchCredentials(LaunchMode.DEVELOPMENT,
                Optional.of("dev-token"), Optional.of("dev-secret"),
                Clock.fixed(start.plus(Duration.ofMinutes(3)), ZoneOffset.UTC));

        assertEquals("dev-token", credentials.issueToken());
        assertTrue(credentials.redeem("dev-token"));
        assertTrue(credentials.redeem("dev-token"));
        assertTrue(threeMinutesLater.redeem("dev-token"));
        assertTrue(credentials.isSessionSecret("dev-secret"));
    }
}
