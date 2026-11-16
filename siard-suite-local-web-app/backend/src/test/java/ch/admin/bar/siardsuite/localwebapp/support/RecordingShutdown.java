package ch.admin.bar.siardsuite.localwebapp.support;

import ch.admin.bar.siardsuite.localwebapp.agent.AgentShutdown;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Records the agent's exit instead of ending the test JVM.
 */
@Alternative
@Priority(1)
@ApplicationScoped
public class RecordingShutdown extends AgentShutdown {

    private volatile CountDownLatch exited = new CountDownLatch(1);

    @Override
    public void exit() {
        exited.countDown();
    }

    public void reset() {
        exited = new CountDownLatch(1);
    }

    public boolean awaitExit(Duration timeout) throws InterruptedException {
        return exited.await(timeout.toMillis(), TimeUnit.MILLISECONDS);
    }
}
