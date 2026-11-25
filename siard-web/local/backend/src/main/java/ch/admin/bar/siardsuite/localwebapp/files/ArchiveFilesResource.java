package ch.admin.bar.siardsuite.localwebapp.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import ch.admin.bar.siardsuite.localwebapp.web.LocalScopeResolver;
import ch.admin.bar.siardsuite.webcore.archive.ArchiveId;
import ch.admin.bar.siardsuite.webcore.archive.ArchiveRegistry;
import ch.admin.bar.siardsuite.webcore.error.ProblemException;

/**
 * Opens a local SIARD archive by its absolute path (design D2): the file is registered in place
 * in this agent's archive scope, never uploaded, copied or changed. Kept outside
 * {@code /api/archives}, whose paths must equal the backend core's, and outside the core, because
 * the remote app obtains archives by upload.
 */
@jakarta.ws.rs.Path("/api/archive-files")
public class ArchiveFilesResource {

    static final String NOT_AN_ARCHIVE = "The file is not a readable SIARD archive";
    static final String NOT_A_FILE = "The path is not a file";

    // the agent does not own the file, so there is nothing to free when the archive is closed
    private static final Runnable NOTHING_TO_RELEASE = () -> { };

    @Inject
    ArchiveRegistry registry;

    @Inject
    LocalScopeResolver scopes;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Open a local SIARD archive in place; browse it under /api/archives/{archiveId}")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Opened",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = OpenedArchive.class))),
            @APIResponse(responseCode = "400", description = "Malformed or relative path, or not a file"),
            @APIResponse(responseCode = "404", description = "Nothing exists at the path"),
            @APIResponse(responseCode = "422", description = "The file is not a readable SIARD archive"),
            @APIResponse(responseCode = "429", description = "Too many open archives")})
    public Response open(OpenArchiveRequest request) {
        Path file = FilePaths.parse(request == null ? null : request.path());
        if (!Files.exists(file)) {
            throw new ProblemException(404, FolderListings.NOT_FOUND);
        }
        if (Files.isDirectory(file)) {
            throw new ProblemException(400, NOT_A_FILE);
        }
        ArchiveId id;
        try {
            // a ProblemException(429) for a reached limit propagates as it is
            id = registry.register(scopes.scope(), file, NOTHING_TO_RELEASE);
        } catch (IOException e) {
            throw new ProblemException(422, NOT_AN_ARCHIVE);
        }
        return Response.status(Response.Status.CREATED)
                .type(MediaType.APPLICATION_JSON)
                .entity(new OpenedArchive(id.value()))
                .build();
    }

    /** The archive file to open. */
    @Schema(description = "The archive file to open")
    public record OpenArchiveRequest(@Schema(required = true, description = "Absolute path") String path) {
    }

    /** The id under which the opened archive is served. */
    @Schema(description = "An opened archive")
    public record OpenedArchive(@Schema(required = true, description = "Use under /api/archives/{archiveId}")
                                String archiveId) {
    }
}
