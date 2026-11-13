package ch.admin.bar.siardsuite.localwebapp.web;

import ch.admin.bar.siardsuite.localwebapp.agent.AgentPort;
import ch.admin.bar.siardsuite.localwebapp.agent.LaunchCredentials;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;

/**
 * Exchanges a launch token for the session cookie (design D4). Every failure gets the same
 * response, so it reveals nothing about the token.
 */
@Path("/launch")
public class LaunchResource {

    @Inject
    LaunchCredentials credentials;

    @Inject
    AgentPort port;

    @GET
    @Operation(hidden = true)
    public Response launch(@QueryParam("token") String token) {
        if (!credentials.redeem(token)) {
            return redirect("/launch-failed").build();
        }
        NewCookie session = new NewCookie.Builder(RequestFilters.sessionCookieName(port.get()))
                .value(credentials.sessionSecret())
                .path("/")
                .httpOnly(true)
                .sameSite(NewCookie.SameSite.STRICT)
                .build();
        return redirect("/").cookie(session).build();
    }

    // Location is set as a raw header: Response.seeOther() would resolve it against the base
    // URI and, behind the Vite dev proxy, send the browser to the backend's own port
    private static Response.ResponseBuilder redirect(String location) {
        return Response.status(Response.Status.SEE_OTHER)
                .header("Location", location)
                .header("Cache-Control", "no-store")
                .header("Referrer-Policy", "no-referrer");
    }
}
