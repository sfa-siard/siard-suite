package ch.admin.bar.siardsuite.webcore.archive;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.Cell;
import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siard2.api.MetaData;
import ch.admin.bar.siard2.api.MetaSchema;
import ch.admin.bar.siard2.api.MetaTable;
import ch.admin.bar.siard2.api.MetaView;
import ch.admin.bar.siard2.api.Schema;
import ch.admin.bar.siard2.api.Table;
import ch.admin.bar.siard2.api.TableRecord;
import ch.admin.bar.siard2.api.facade.MetaTableFacade;
import ch.admin.bar.siard2.api.facade.TableRecordFacade;
import ch.admin.bar.siardsuite.webcore.archive.ArchiveDtos.ArchiveDto;
import ch.admin.bar.siardsuite.webcore.archive.ArchiveDtos.CellDto;
import ch.admin.bar.siardsuite.webcore.archive.ArchiveDtos.ColumnDto;
import ch.admin.bar.siardsuite.webcore.archive.ArchiveDtos.RowDto;
import ch.admin.bar.siardsuite.webcore.archive.ArchiveDtos.RowsDto;
import ch.admin.bar.siardsuite.webcore.archive.ArchiveDtos.SchemaDto;
import ch.admin.bar.siardsuite.webcore.archive.ArchiveDtos.SchemaRef;
import ch.admin.bar.siardsuite.webcore.archive.ArchiveDtos.SearchDto;
import ch.admin.bar.siardsuite.webcore.archive.ArchiveDtos.TableDto;
import ch.admin.bar.siardsuite.webcore.archive.ArchiveDtos.TableRef;
import ch.admin.bar.siardsuite.webcore.archive.ArchiveDtos.ViewDto;
import ch.admin.bar.siardsuite.webcore.archive.ArchiveDtos.ViewRef;
import ch.admin.bar.siardsuite.webcore.error.ProblemException;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.server.spi.ServerRequestContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.IntStream;

/**
 * The read-only archive API (spec archive-browsing) and explicit close (spec archive-registry).
 * Schemas, tables, views and columns are addressed by zero-based index (design D8); every request
 * holds the archive's lock while it reads (design D3).
 */
@Path("/api/archives/{archiveId}")
@Produces(MediaType.APPLICATION_JSON)
public class ArchiveResource {

    static final int DEFAULT_LIMIT = 100;
    static final int MAX_LIMIT = 1000;

    @Inject
    ArchiveRegistry registry;

    @Context
    ContainerRequestContext request;

    @Context
    ServerRequestContext requestContext;

    @ConfigProperty(name = "siard.archives.search.max-rows", defaultValue = "20000")
    long searchMaxRows;

    @ConfigProperty(name = "siard.archives.search.max-time", defaultValue = "PT5S")
    Duration searchMaxTime;

    @PathParam("archiveId")
    String archiveId;

    @GET
    public ArchiveDto archive() {
        try (ArchiveLease lease = acquire()) {
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
    public void close() {
        registry.close(ArchiveScopeFilter.scopeOf(request), new ArchiveId(archiveId));
    }

    @GET
    @Path("/schemas/{s}")
    public SchemaDto schema(@PathParam("s") int s) {
        try (ArchiveLease lease = acquire()) {
            Schema schema = schema(lease, s);
            MetaSchema ms = schema.getMetaSchema();
            List<TableRef> tables = IntStream.range(0, schema.getTables())
                    .mapToObj(t -> {
                        MetaTable mt = schema.getTable(t).getMetaTable();
                        return new TableRef(t, mt.getName(), mt.getRows());
                    })
                    .toList();
            List<ViewRef> views = IntStream.range(0, ms.getMetaViews())
                    .mapToObj(v -> new ViewRef(v, ms.getMetaView(v).getName()))
                    .toList();
            return new SchemaDto(s, ms.getName(), ms.getDescription(), tables, views);
        }
    }

    @GET
    @Path("/schemas/{s}/tables/{t}")
    public TableDto table(@PathParam("s") int s, @PathParam("t") int t) throws IOException {
        try (ArchiveLease lease = acquire()) {
            MetaTable mt = table(schema(lease, s), t).getMetaTable();
            List<MetaColumn> metaColumns = new MetaTableFacade(mt).getMetaColums();
            List<ColumnDto> columns = new ArrayList<>();
            for (int c = 0; c < metaColumns.size(); c++) {
                columns.add(column(c, metaColumns.get(c)));
            }
            return new TableDto(t, mt.getName(), mt.getDescription(), mt.getRows(), columns);
        }
    }

    @GET
    @Path("/schemas/{s}/views/{v}")
    public ViewDto view(@PathParam("s") int s, @PathParam("v") int v) throws IOException {
        try (ArchiveLease lease = acquire()) {
            MetaSchema ms = schema(lease, s).getMetaSchema();
            if (v < 0 || v >= ms.getMetaViews()) {
                throw notFound();
            }
            MetaView mv = ms.getMetaView(v);
            List<ColumnDto> columns = new ArrayList<>();
            for (int c = 0; c < mv.getMetaColumns(); c++) {
                columns.add(column(c, mv.getMetaColumn(c)));
            }
            return new ViewDto(v, mv.getName(), mv.getDescription(), mv.getQuery(), columns);
        }
    }

    @GET
    @Path("/schemas/{s}/tables/{t}/rows")
    public RowsDto rows(@PathParam("s") int s, @PathParam("t") int t,
                        @QueryParam("offset") String offsetParam, @QueryParam("limit") String limitParam)
            throws IOException {
        long offset = parse(offsetParam, 0, "offset");
        long limit = parse(limitParam, DEFAULT_LIMIT, "limit");
        if (offset < 0) {
            throw new ProblemException(400, "offset must not be negative");
        }
        if (limit < 1 || limit > MAX_LIMIT) {
            throw new ProblemException(400, "limit must be between 1 and " + MAX_LIMIT);
        }
        try (ArchiveLease lease = acquire()) {
            Table table = table(schema(lease, s), t);
            List<TableRecord> records = lease.cursors().page(table, offset, (int) limit);
            List<RowDto> rows = new ArrayList<>(records.size());
            for (int i = 0; i < records.size(); i++) {
                TableRecord record = records.get(i);
                List<CellDto> cells = new ArrayList<>();
                for (Cell cell : new TableRecordFacade(record).getCells()) {
                    cells.add(CellRenderer.render(cell));
                }
                rows.add(new RowDto(offset + i, cells));
            }
            return new RowsDto(table.getMetaTable().getRows(), offset, rows);
        }
    }

    static final int MAX_QUERY = 200;

    /**
     * Finds the next row after {@code afterRow} with a cell whose displayed text contains {@code q},
     * within the search budget; the lock is released between requests (design D5).
     */
    @GET
    @Path("/schemas/{s}/tables/{t}/search")
    public SearchDto search(@PathParam("s") int s, @PathParam("t") int t, @QueryParam("q") String q,
                            @QueryParam("matchCase") String matchCaseParam,
                            @QueryParam("afterRow") String afterRowParam) throws IOException {
        if (q == null || q.isEmpty() || q.length() > MAX_QUERY) {
            throw new ProblemException(400, "q must have 1 to " + MAX_QUERY + " characters");
        }
        boolean matchCase = Boolean.parseBoolean(matchCaseParam);
        long afterRow = parse(afterRowParam, -1, "afterRow");
        if (afterRow < -1) {
            throw new ProblemException(400, "afterRow must be -1 or more");
        }
        try (ArchiveLease lease = acquire()) {
            Table table = table(schema(lease, s), t);
            return SearchScan.run(lease.cursors(), table, afterRow + 1, q, matchCase, searchMaxRows, searchMaxTime);
        }
    }

    /**
     * Streams a cell's full content as an attachment (design D7). The archive's lock is held until
     * the last byte is written, and released when the request completes, whether the body was
     * written, failed, aborted or never written at all ({@code HEAD}, client gone before writing).
     */
    @GET
    @Path("/schemas/{s}/tables/{t}/rows/{row}/columns/{c}/content")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response content(@PathParam("s") int s, @PathParam("t") int t, @PathParam("row") long row,
                            @PathParam("c") int c) throws IOException {
        ArchiveLease lease = acquire();
        requestContext.registerCompletionCallback(failure -> lease.close());
        Table table = table(schema(lease, s), t);
        if (row < 0 || row >= table.getMetaTable().getRows()) {
            throw notFound();
        }
        TableRecord record = lease.cursors().page(table, row, 1).getFirst();
        List<Cell> cells = new TableRecordFacade(record).getCells();
        if (c < 0 || c >= cells.size()) {
            throw notFound();
        }
        Cell cell = cells.get(c);
        if (cell.isNull()) {
            throw notFound();
        }
        StreamingOutput body = output -> write(cell, output);
        return Response.ok(body, MediaType.APPLICATION_OCTET_STREAM_TYPE)
                .header("Content-Disposition", "attachment; filename=\"t" + t + "-r" + row + "-c" + c + ".bin\"")
                .header("X-Content-Type-Options", "nosniff")
                .build();
    }

    /** Binary content as stored, text as UTF-8, the encoding siard-api stores text LOBs in (S3). */
    private static void write(Cell cell, OutputStream output) throws IOException {
        if (CellRenderer.isBinary(cell)) {
            try (InputStream input = cell.getInputStream()) {
                input.transferTo(output);
            }
            return;
        }
        Writer writer = new OutputStreamWriter(output, StandardCharsets.UTF_8);
        if (CellRenderer.isReaderType(cell)) {
            try (Reader reader = cell.getReader()) {
                reader.transferTo(writer);
            }
        } else {
            writer.write(CellRenderer.text(cell, Integer.MAX_VALUE));
        }
        writer.flush();
    }

    private ArchiveLease acquire() {
        return registry.acquire(ArchiveScopeFilter.scopeOf(request), new ArchiveId(archiveId));
    }

    static Schema schema(ArchiveLease lease, int s) {
        Archive archive = lease.archive();
        if (s < 0 || s >= archive.getSchemas()) {
            throw notFound();
        }
        return archive.getSchema(s);
    }

    static Table table(Schema schema, int t) {
        if (t < 0 || t >= schema.getTables()) {
            throw notFound();
        }
        return schema.getTable(t);
    }

    private static ColumnDto column(int index, MetaColumn mc) throws IOException {
        int type = mc.getPreType();
        boolean lob = type == java.sql.Types.BINARY || type == java.sql.Types.VARBINARY || type == java.sql.Types.BLOB;
        return new ColumnDto(index, mc.getName(), mc.getType(), mc.isNullable(), mc.getDescription(), lob);
    }

    private static long parse(String value, long defaultValue, String name) {
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ProblemException(400, name + " must be a whole number");
        }
    }

    static ProblemException notFound() {
        return new ProblemException(404, null);
    }

    private static LocalDate toLocalDate(Calendar calendar) {
        return calendar == null ? null : LocalDate.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
    }
}
