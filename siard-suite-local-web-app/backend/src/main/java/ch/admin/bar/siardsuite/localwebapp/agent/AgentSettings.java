package ch.admin.bar.siardsuite.localwebapp.agent;

import io.quarkus.runtime.LaunchMode;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.Optional;

/**
 * Behaviour flags whose defaults depend on the launch mode but which can be set in any mode
 * (design D8): only the packaged app opens a browser and exits when idle by default.
 */
@ApplicationScoped
public class AgentSettings {

    static final Duration DEFAULT_IDLE_EXIT_GRACE = Duration.ofSeconds(20);

    @ConfigProperty(name = "siard.agent.open-browser")
    Optional<Boolean> openBrowser;

    @ConfigProperty(name = "siard.agent.idle-exit-grace")
    Optional<Duration> idleExitGrace;

    public boolean openBrowser() {
        return openBrowser.orElse(LaunchMode.current() == LaunchMode.NORMAL);
    }

    /**
     * The grace period before exiting without a connected window, or empty if idle exit is disabled.
     */
    public Optional<Duration> idleExitGrace() {
        if (idleExitGrace.isPresent()) {
            return idleExitGrace;
        }
        return LaunchMode.current() == LaunchMode.NORMAL ? Optional.of(DEFAULT_IDLE_EXIT_GRACE) : Optional.empty();
    }
}
