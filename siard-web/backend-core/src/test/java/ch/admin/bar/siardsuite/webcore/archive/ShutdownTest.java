package ch.admin.bar.siardsuite.webcore.archive;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.fixture;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.newScope;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Spec requirement "Open archives are released on shutdown". The test application keeps running,
 * so the test delivers the shutdown event to the registry's observer itself.
 */
@QuarkusTest
class ShutdownTest {

    @Inject
    ArchiveRegistry registry;

    @Test
    void shutdownWithOpenArchives() throws IOException {
        List<AtomicInteger> releases = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            AtomicInteger counter = new AtomicInteger();
            releases.add(counter);
            registry.register(newScope(), fixture("sample.siard"), counter::incrementAndGet);
        }

        registry.onShutdown(new ShutdownEvent());

        for (AtomicInteger counter : releases) {
            assertEquals(1, counter.get());
        }
    }
}
