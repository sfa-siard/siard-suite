package ch.admin.bar.siardsuite.webcore.archive;

import jakarta.inject.Inject;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test-only registration endpoint, standing in for an app's own (a picker locally, an upload
 * remotely), so that a rejected registration can be observed as an HTTP response. Counts the
 * release callbacks per {@code tag}.
 */
@Path("/api/test-archives")
@Produces(MediaType.APPLICATION_JSON)
public class TestRegistrationResource {

    public static final Map<String, AtomicInteger> RELEASES = new ConcurrentHashMap<>();

    public record Registered(String archiveId) {
    }

    @Inject
    ArchiveRegistry registry;

    @POST
    public Registered register(@HeaderParam(TestScopeResolver.HEADER) String scope,
                               @QueryParam("fixture") String fixture, @QueryParam("tag") String tag)
            throws IOException {
        AtomicInteger releases = RELEASES.computeIfAbsent(tag, t -> new AtomicInteger());
        ArchiveId id = registry.register(new ArchiveScope(scope), TestArchives.fixture(fixture),
                releases::incrementAndGet);
        return new Registered(id.value());
    }
}
