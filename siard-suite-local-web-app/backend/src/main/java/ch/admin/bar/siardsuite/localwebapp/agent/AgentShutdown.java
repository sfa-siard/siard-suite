package ch.admin.bar.siardsuite.localwebapp.agent;

import io.quarkus.runtime.Quarkus;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Stops the agent; a bean of its own so tests can replace it instead of ending the test JVM.
 */
@ApplicationScoped
public class AgentShutdown {

    public void exit() {
        Quarkus.asyncExit();
    }
}
