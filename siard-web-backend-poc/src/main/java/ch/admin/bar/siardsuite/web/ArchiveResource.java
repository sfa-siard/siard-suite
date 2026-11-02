package ch.admin.bar.siardsuite.web;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siard2.api.MetaData;
import ch.admin.bar.siard2.api.MetaSchema;
import ch.admin.bar.siard2.api.MetaTable;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Path("/api/archives")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArchiveResource {

    private static final Logger LOG = LoggerFactory.getLogger(ArchiveResource.class);

    @Inject
    ArchiveRegistry registry;

    @POST
    public Response open(OpenRequest request) {
        if (request == null || request.path() == null || request.path().isBlank()) {
            return badRequest("path is required");
        }
        try {
            ArchiveRegistry.OpenedArchive opened = registry.open(java.nio.file.Path.of(request.path()).toAbsolutePath().normalize());
            return Response.status(Response.Status.CREATED)
                    .entity(new OpenResponse(opened.id(), opened.name()))
                    .build();
        } catch (Exception e) {
            LOG.warn("could not open archive at {}: {}", request.path(), e.toString());
            return badRequest("could not open archive");
        }
    }

    @GET
    @Path("/{id}/tree")
    public Response tree(@PathParam("id") String id) {
        Archive archive = registry.get(id);
        if (archive == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("unknown archive id: " + id))
                    .build();
        }
        return Response.ok(new TreeResponse(schemas(archive.getMetaData()))).build();
    }

    private static List<SchemaDto> schemas(MetaData metaData) {
        List<SchemaDto> schemas = new ArrayList<>();
        for (int i = 0; i < metaData.getMetaSchemas(); i++) {
            MetaSchema metaSchema = metaData.getMetaSchema(i);
            schemas.add(new SchemaDto(metaSchema.getName(), tables(metaSchema)));
        }
        return schemas;
    }

    private static List<TableDto> tables(MetaSchema metaSchema) {
        List<TableDto> tables = new ArrayList<>();
        for (int i = 0; i < metaSchema.getMetaTables(); i++) {
            MetaTable metaTable = metaSchema.getMetaTable(i);
            tables.add(new TableDto(metaTable.getName(), metaTable.getRows(), columns(metaTable)));
        }
        return tables;
    }

    private static List<ColumnDto> columns(MetaTable metaTable) {
        List<ColumnDto> columns = new ArrayList<>();
        for (int i = 0; i < metaTable.getMetaColumns(); i++) {
            MetaColumn metaColumn = metaTable.getMetaColumn(i);
            columns.add(new ColumnDto(metaColumn.getName(), metaColumn.getTypeOriginal()));
        }
        return columns;
    }

    private static Response badRequest(String message) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(message))
                .build();
    }

    public record OpenRequest(String path) {
    }

    public record OpenResponse(String id, String name) {
    }

    public record ErrorResponse(String error) {
    }

    public record ColumnDto(String name, String type) {
    }

    public record TableDto(String name, long rowCount, List<ColumnDto> columns) {
    }

    public record SchemaDto(String name, List<TableDto> tables) {
    }

    public record TreeResponse(List<SchemaDto> schemas) {
    }
}
