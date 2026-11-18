package ch.admin.bar.siardsuite.webcore;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import java.time.Clock;

/**
 * Produces the clock of every app built on the core; tests replace it with {@code MutableClock}
 * from the core's test fixtures (design D2).
 */
@ApplicationScoped
public class CoreBeans {

    @Produces
    @ApplicationScoped
    Clock clock() {
        return Clock.systemUTC();
    }
}
