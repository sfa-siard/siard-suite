package ch.admin.bar.siardsuite.localwebapp.files;

import java.nio.file.Files;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import ch.admin.bar.siardsuite.webcore.error.ProblemException;

/**
 * The local file system as the file chooser sees it (design D1, D2). Session, Host and
 * {@code Sec-Fetch-Site} checks apply through the agent's request filters; errors are RFC 9457
 * problem details that carry at most the path the client sent. Clients decide by status code.
 */
@Path("/api/files")
public class FilesResource {

    @Inject
    FolderListings listings;

    @Inject
    Places places;

    @Inject
    FileManagerLauncher fileManager;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "List a folder: folders first, then files, each by name ignoring case; "
            + "at most 10000 entries")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK"),
            @APIResponse(responseCode = "400", description = "Malformed or relative path, or not a folder"),
            @APIResponse(responseCode = "403", description = "The folder may not be read"),
            @APIResponse(responseCode = "404", description = "Nothing exists at the path"),
            @APIResponse(responseCode = "503", description = "All listing workers are busy"),
            @APIResponse(responseCode = "504", description = "The location did not respond within 10 seconds")})
    public FolderListing list(
            @QueryParam("path") @Parameter(required = true, description = "Absolute folder path") String path,
            @QueryParam("extension") @Parameter(description = "Keep only files ending with one of these, "
                    + "ignoring case, e.g. .siard") List<String> extensions,
            @QueryParam("foldersOnly") @Parameter(description = "Leave out all files") boolean foldersOnly,
            @QueryParam("hidden") @Parameter(description = "Include hidden entries") boolean hidden) {
        return listings.list(FilePaths.parse(path), new ListingFilter(extensions, foldersOnly, hidden));
    }

    @GET
    @Path("/places")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Starting places: home, Desktop, Documents, Downloads (if they exist) and the roots")
    public List<Place> places() {
        return places.places();
    }

    @POST
    @Path("/reveal")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Show a file selected in the system file manager, or open a folder there")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "The file manager was started"),
            @APIResponse(responseCode = "400", description = "Malformed or relative path"),
            @APIResponse(responseCode = "404", description = "Nothing exists at the path"),
            @APIResponse(responseCode = "503", description = "No file manager could be started")})
    public Response reveal(RevealRequest request) {
        java.nio.file.Path parsed = FilePaths.parse(request == null ? null : request.path());
        if (!Files.exists(parsed)) {
            throw new ProblemException(404, FolderListings.NOT_FOUND);
        }
        fileManager.reveal(parsed, Files.isDirectory(parsed));
        return Response.noContent().build();
    }

    @GET
    @Path("/entry")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Look up one file or folder by its absolute path")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "OK"),
            @APIResponse(responseCode = "400", description = "Malformed or relative path"),
            @APIResponse(responseCode = "404", description = "Nothing exists at the path")})
    public EntryInfo entry(
            @QueryParam("path") @Parameter(required = true, description = "Absolute path") String path) {
        java.nio.file.Path parsed = FilePaths.parse(path);
        FileEntry entry = FolderLister.entry(parsed)
                .orElseThrow(() -> new ProblemException(404, FolderListings.NOT_FOUND));
        return new EntryInfo(entry.path(), entry.name(), FilePaths.parentOf(parsed), entry.folder(),
                entry.size(), entry.modified(), entry.hidden(), Files.isWritable(parsed));
    }
}
