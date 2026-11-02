package ch.admin.bar.siardsuite.web;

import io.quarkus.runtime.Quarkus;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Schedules agent termination. Indirection exists so tests can substitute a
 * no-op shutdown instead of testing production code against a launch-mode
 * check inside the resource.
 */
@ApplicationScoped
public class AgentShutdown {

    public void scheduleExit() {
        CompletableFuture.delayedExecutor(300, TimeUnit.MILLISECONDS)
                .execute(() -> Quarkus.asyncExit(0));
    }
}
