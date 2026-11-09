package ch.admin.bar.siardsuite.localwebapp.support;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Map;

/**
 * Test-only API endpoint: the real {@code /api/system/info} arrives with task 7.1.
 */
@Path("/api/test-probe")
@Produces(MediaType.APPLICATION_JSON)
public class ProbeResource {

    public static final String INTERNAL_MESSAGE = "probe-internal-secret-42";

    @GET
    public Map<String, Boolean> ok() {
        return Map.of("ok", true);
    }

    @GET
    @Path("/fail")
    public Map<String, Boolean> fail() {
        throw new IllegalStateException(INTERNAL_MESSAGE);
    }
}
