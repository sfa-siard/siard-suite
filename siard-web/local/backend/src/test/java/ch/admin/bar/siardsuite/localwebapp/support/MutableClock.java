package ch.admin.bar.siardsuite.localwebapp.support;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * Replaces the agent's clock in tests so token expiry can be tested without waiting.
 */
@Alternative
@Priority(1)
@ApplicationScoped
public class MutableClock extends Clock {

    private volatile Instant now = Instant.now();

    public void advance(Duration duration) {
        now = now.plus(duration);
    }

    @Override
    public Instant instant() {
        return now;
    }

    @Override
    public ZoneId getZone() {
        return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return this;
    }
}
