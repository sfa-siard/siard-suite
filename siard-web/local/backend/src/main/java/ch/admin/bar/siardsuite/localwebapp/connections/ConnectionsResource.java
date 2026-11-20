package ch.admin.bar.siardsuite.localwebapp.connections;

import ch.admin.bar.siardsuite.localwebapp.database.Dbms;
import ch.admin.bar.siardsuite.localwebapp.database.DbmsRegistry;
import ch.admin.bar.siardsuite.localwebapp.database.ServerBasedDbms;
import ch.admin.bar.siardsuite.webcore.error.ProblemDetail;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import java.util.List;
import java.util.Optional;

/**
 * Saved connection profiles of the local app (design D5): list, create and delete. Session,
 * Host and {@code Sec-Fetch-Site} checks apply through the agent's request filters; errors are
 * RFC 9457 problem details. No payload ever contains a password.
 */
@Path("/api/connections")
public class ConnectionsResource {

    @Inject
    ConnectionStore store;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "All saved connections, ordered by name")
    public List<SavedConnection> list() {
        return store.list();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Save a connection profile; the name must be unique")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = SavedConnection.class))),
            @APIResponse(responseCode = "400", description = "Bad Request"),
            @APIResponse(responseCode = "409", description = "Conflict")})
    public Response create(SaveConnectionRequest request) {
        if (request == null) {
            return problem(400, "A JSON request body is required");
        }
        String name = trimmed(request.name());
        if (name == null) {
            return problem(400, "name is required");
        }
        Optional<Dbms<?>> dbms = DbmsRegistry.findById(request.dbmsId());
        if (dbms.isEmpty() || !(dbms.get() instanceof ServerBasedDbms)) {
            return problem(400, "unknown dbmsId");
        }
        if (trimmed(request.host()) == null) {
            return problem(400, "host is required");
        }
        String port = trimmed(request.port());
        if (port == null || !port.matches("\\d+")) {
            return problem(400, "port must be a number");
        }
        if (trimmed(request.dbName()) == null) {
            return problem(400, "dbName is required");
        }
        if (trimmed(request.user()) == null) {
            return problem(400, "user is required");
        }
        try {
            SavedConnection saved = store.create(new NewConnection(
                    name,
                    request.dbmsId(),
                    request.host().trim(),
                    port,
                    request.dbName().trim(),
                    request.user().trim(),
                    trimmed(request.options()),
                    null));
            return Response.status(Response.Status.CREATED)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(saved)
                    .build();
        } catch (DuplicateConnectionNameException e) {
            return problem(409, "A connection with this name already exists");
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a saved connection by id")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Deleted"),
            @APIResponse(responseCode = "404", description = "Not Found")})
    public Response delete(@PathParam("id") String id) {
        long connectionId;
        try {
            connectionId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return problem(404, "Unknown connection");
        }
        return store.delete(connectionId)
                ? Response.noContent().build()
                : problem(404, "Unknown connection");
    }

    private static Response problem(int status, String detail) {
        return Response.status(status)
                .type(ProblemDetail.MEDIA_TYPE)
                .entity(ProblemDetail.of(status, detail))
                .build();
    }

    private static String trimmed(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
