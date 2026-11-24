package ch.admin.bar.siardsuite.webcore.error;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Map;

/**
 * Test-only API endpoints whose handlers throw, for the error-response scenarios.
 */
@Path("/api/test-probe")
@Produces(MediaType.APPLICATION_JSON)
public class ProbeResource {

    public static final String INTERNAL_MESSAGE = "probe-internal-secret-42";

    @GET
    @Path("/fail")
    public Map<String, Boolean> fail() {
        throw new IllegalStateException(INTERNAL_MESSAGE);
    }

    @GET
    @Path("/problem")
    public Map<String, Boolean> problem() {
        throw new ProblemException(400, "D");
    }
}
