package ch.admin.bar.siardsuite.web;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

/**
 * No-op shutdown used in tests — the resource's 204 contract is exercised
 * without terminating the test application.
 */
@Alternative
@Priority(1)
@ApplicationScoped
public class TestAgentShutdown extends AgentShutdown {

    @Override
    public void scheduleExit() {
        // no-op: keep the test JVM alive
    }
}
