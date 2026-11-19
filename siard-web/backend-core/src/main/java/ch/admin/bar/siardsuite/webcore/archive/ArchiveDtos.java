package ch.admin.bar.siardsuite.webcore.archive;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.eclipse.microprofile.openapi.annotations.media.DiscriminatorMapping;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

/**
 * The response bodies of the archive API: records mapped from siard-api, which itself is never
 * serialised (design D6).
 */
public final class ArchiveDtos {

    private ArchiveDtos() {
    }

    public record SchemaRef(int index, String name) {
    }

    public record ArchiveDto(String dbName, String description, String dataOwner, String dataOriginTimespan,
                             String archiver, String archiverContact, LocalDate archivalDate, String version,
                             List<SchemaRef> schemas) {
    }

    public record TableRef(int index, String name, long rows) {
    }

    public record ViewRef(int index, String name) {
    }

    public record SchemaDto(int index, String name, String description, List<TableRef> tables, List<ViewRef> views) {
    }

    public record ColumnDto(int index, String name, String type, boolean nullable, String description, boolean lob) {
    }

    public record TableDto(int index, String name, String description, long rows, List<ColumnDto> columns) {
    }

    public record ViewDto(int index, String name, String description, String query, List<ColumnDto> columns) {
    }

    /** A cell: JSON {@code null} for SQL NULL, otherwise text or a large object (design D6). */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = TextCell.class, name = "text"),
            @JsonSubTypes.Type(value = LobCell.class, name = "lob")})
    @Schema(oneOf = {TextCell.class, LobCell.class}, discriminatorProperty = "type",
            discriminatorMapping = {
                    @DiscriminatorMapping(value = "text", schema = TextCell.class),
                    @DiscriminatorMapping(value = "lob", schema = LobCell.class)})
    public sealed interface CellDto permits TextCell, LobCell {
    }

    public record TextCell(String value, boolean truncated) implements CellDto {
    }

    public record LobCell(long length) implements CellDto {
    }

    public record RowDto(long row, List<CellDto> cells) {
    }

    public record RowsDto(long total, long offset, List<RowDto> rows) {
    }

    /**
     * Result of one bounded search request (design D5): the match, or {@code complete} when the
     * table end was reached, or {@code nextRow} where to continue when the budget ran out.
     */
    public record SearchDto(boolean found, Long row, Integer column, boolean complete, Long nextRow) {
    }
}
