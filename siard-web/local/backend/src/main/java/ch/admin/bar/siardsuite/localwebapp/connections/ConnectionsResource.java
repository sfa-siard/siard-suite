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
import jakarta.ws.rs.PUT;
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
import java.util.OptionalLong;

/**
 * Saved connection profiles of the local app (design D5): list, create, update and delete. Session,
 * Host and {@code Sec-Fetch-Site} checks apply through the agent's request filters; errors are
 * RFC 9457 problem details. No payload ever contains a password.
 */
@Path("/api/connections")
public class ConnectionsResource {

    private static final String DUPLICATE_NAME = "A connection with this name already exists";
    private static final String UNKNOWN_CONNECTION = "Unknown connection";
    private static final int MIN_PORT = 1;
    private static final int MAX_PORT = 65535;

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
        Validated validated = validate(request);
        if (validated.problem() != null) {
            return validated.problem();
        }
        try {
            SavedConnection saved = store.create(validated.connection());
            return Response.status(Response.Status.CREATED)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(saved)
                    .build();
        } catch (DuplicateConnectionNameException e) {
            return problem(409, DUPLICATE_NAME);
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update a saved connection profile; the name must stay unique")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Updated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = SavedConnection.class))),
            @APIResponse(responseCode = "400", description = "Bad Request"),
            @APIResponse(responseCode = "404", description = "Not Found"),
            @APIResponse(responseCode = "409", description = "Conflict")})
    public Response update(@PathParam("id") String id, SaveConnectionRequest request) {
        OptionalLong connectionId = parseId(id);
        if (connectionId.isEmpty()) {
            return problem(404, UNKNOWN_CONNECTION);
        }
        Validated validated = validate(request);
        if (validated.problem() != null) {
            return validated.problem();
        }
        try {
            return store.update(connectionId.getAsLong(), validated.connection())
                    .map(saved -> Response.ok(saved, MediaType.APPLICATION_JSON).build())
                    .orElseGet(() -> problem(404, UNKNOWN_CONNECTION));
        } catch (DuplicateConnectionNameException e) {
            return problem(409, DUPLICATE_NAME);
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a saved connection by id")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Deleted"),
            @APIResponse(responseCode = "404", description = "Not Found")})
    public Response delete(@PathParam("id") String id) {
        OptionalLong connectionId = parseId(id);
        return connectionId.isPresent() && store.delete(connectionId.getAsLong())
                ? Response.noContent().build()
                : problem(404, UNKNOWN_CONNECTION);
    }

    /** A validated request: either the profile to store or the 400 problem to return. */
    private record Validated(NewConnection connection, Response problem) {

        static Validated invalid(String detail) {
            return new Validated(null, ConnectionsResource.problem(400, detail));
        }
    }

    private static Validated validate(SaveConnectionRequest request) {
        if (request == null) {
            return Validated.invalid("A JSON request body is required");
        }
        String name = trimmed(request.name());
        if (name == null) {
            return Validated.invalid("name is required");
        }
        Optional<Dbms<?>> dbms = DbmsRegistry.findById(request.dbmsId());
        if (dbms.isEmpty() || !(dbms.get() instanceof ServerBasedDbms)) {
            return Validated.invalid("unknown dbmsId");
        }
        if (trimmed(request.host()) == null) {
            return Validated.invalid("host is required");
        }
        String port = trimmed(request.port());
        if (!isValidPort(port)) {
            return Validated.invalid("port must be a number between 1 and 65535");
        }
        if (trimmed(request.dbName()) == null) {
            return Validated.invalid("dbName is required");
        }
        if (trimmed(request.user()) == null) {
            return Validated.invalid("user is required");
        }
        return new Validated(new NewConnection(
                name,
                request.dbmsId(),
                request.host().trim(),
                port,
                request.dbName().trim(),
                request.user().trim(),
                trimmed(request.options()),
                null), null);
    }

    private static boolean isValidPort(String port) {
        // at most five digits, so parsing cannot overflow
        if (port == null || !port.matches("\\d{1,5}")) {
            return false;
        }
        int value = Integer.parseInt(port);
        return value >= MIN_PORT && value <= MAX_PORT;
    }

    private static OptionalLong parseId(String id) {
        try {
            return OptionalLong.of(Long.parseLong(id));
        } catch (NumberFormatException e) {
            return OptionalLong.empty();
        }
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
