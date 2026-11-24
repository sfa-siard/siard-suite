package ch.admin.bar.siardsuite.localwebapp.agent;

import io.quarkus.runtime.LaunchMode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Clock;
import java.util.Optional;

/**
 * Produces the agent's launch credentials; the clock comes from the backend core.
 */
@ApplicationScoped
public class AgentBeans {

    @Produces
    @ApplicationScoped
    LaunchCredentials launchCredentials(
            Clock clock,
            @ConfigProperty(name = "siard.agent.dev-token") Optional<String> devToken,
            @ConfigProperty(name = "siard.agent.dev-session-secret") Optional<String> devSessionSecret) {
        return new LaunchCredentials(LaunchMode.current(), devToken, devSessionSecret, clock);
    }
}
