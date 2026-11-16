package ch.admin.bar.siardsuite.localwebapp.agent;

import io.quarkus.runtime.StartupEvent;
import io.vertx.core.Vertx;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

/**
 * Counts the open authorized event streams, one per browser window (design D5). When the
 * count drops to 0 the exit timer starts; a new stream cancels it. If no window connects
 * before the launch token has expired (plus the grace period), nobody can obtain a session
 * any more, so the agent exits as well. Both apply only while idle exit is enabled.
 */
@ApplicationScoped
public class Presence {

    private static final Logger LOG = LoggerFactory.getLogger(Presence.class);
    private static final Duration FIRST_WINDOW_CHECK_INTERVAL = Duration.ofSeconds(1);

    @Inject
    Vertx vertx;

    @Inject
    Clock clock;

    @Inject
    AgentSettings settings;

    @Inject
    AgentShutdown shutdown;

    private int openStreams;
    private Long exitTimer;
    private Long firstWindowCheck;

    // checked against the agent clock (like the token expiry itself), so tests can advance it
    void awaitFirstWindow(@Observes StartupEvent startup) {
        settings.idleExitGrace().ifPresent(grace -> {
            Instant deadline = clock.instant().plus(LaunchCredentials.TOKEN_LIFETIME).plus(grace);
            synchronized (this) {
                firstWindowCheck = vertx.setPeriodic(FIRST_WINDOW_CHECK_INTERVAL.toMillis(),
                        id -> exitIfNoWindowBy(deadline));
            }
        });
    }

    public synchronized void streamOpened() {
        openStreams++;
        if (firstWindowCheck != null) {
            vertx.cancelTimer(firstWindowCheck);
            firstWindowCheck = null;
        }
        if (exitTimer != null) {
            vertx.cancelTimer(exitTimer);
            exitTimer = null;
        }
    }

    public synchronized void streamClosed() {
        openStreams--;
        if (openStreams == 0) {
            settings.idleExitGrace().ifPresent(this::startExitTimer);
        }
    }

    private void startExitTimer(Duration grace) {
        exitTimer = vertx.setTimer(Math.max(1, grace.toMillis()), this::exitIfStillIdle);
    }

    private synchronized void exitIfNoWindowBy(Instant deadline) {
        if (firstWindowCheck != null && clock.instant().isAfter(deadline)) {
            vertx.cancelTimer(firstWindowCheck);
            firstWindowCheck = null;
            LOG.info("No SIARD Suite window connected before the launch link expired; exiting");
            shutdown.exit();
        }
    }

    private synchronized void exitIfStillIdle(long timerId) {
        if (exitTimer != null && exitTimer == timerId) {
            exitTimer = null;
            LOG.info("No SIARD Suite window connected; exiting");
            shutdown.exit();
        }
    }
}
