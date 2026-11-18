package ch.admin.bar.siardsuite.webcore.archive;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.MetaData;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.stream.IntStream;

/**
 * The read-only archive API (spec archive-browsing) and explicit close (spec archive-registry).
 */
@Path("/api/archives/{archiveId}")
@Produces(MediaType.APPLICATION_JSON)
public class ArchiveResource {

    public record SchemaRef(int index, String name) {
    }

    public record ArchiveDto(String dbName, String description, String dataOwner, String dataOriginTimespan,
                             String archiver, String archiverContact, LocalDate archivalDate, String version,
                             List<SchemaRef> schemas) {
    }

    @Inject
    ArchiveRegistry registry;

    @GET
    public ArchiveDto archive(@PathParam("archiveId") String archiveId, @Context ContainerRequestContext request) {
        try (ArchiveLease lease = registry.acquire(ArchiveScopeFilter.scopeOf(request), new ArchiveId(archiveId))) {
            Archive archive = lease.archive();
            MetaData md = archive.getMetaData();
            List<SchemaRef> schemas = IntStream.range(0, archive.getSchemas())
                    .mapToObj(i -> new SchemaRef(i, archive.getSchema(i).getMetaSchema().getName()))
                    .toList();
            return new ArchiveDto(md.getDbName(), md.getDescription(), md.getDataOwner(), md.getDataOriginTimespan(),
                    md.getArchiver(), md.getArchiverContact(), toLocalDate(md.getArchivalDate()), md.getVersion(),
                    schemas);
        }
    }

    @DELETE
    public void close(@PathParam("archiveId") String archiveId, @Context ContainerRequestContext request) {
        registry.close(ArchiveScopeFilter.scopeOf(request), new ArchiveId(archiveId));
    }

    private static LocalDate toLocalDate(Calendar calendar) {
        return calendar == null ? null : LocalDate.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
    }
}
