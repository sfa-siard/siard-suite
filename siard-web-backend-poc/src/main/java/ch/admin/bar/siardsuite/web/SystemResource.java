package ch.admin.bar.siardsuite.web;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/api/system")
public class SystemResource {

    @Inject
    AgentShutdown agentShutdown;

    @POST
    @Path("/shutdown")
    public Response shutdown() {
        agentShutdown.scheduleExit();
        return Response.noContent().build();
    }
}
