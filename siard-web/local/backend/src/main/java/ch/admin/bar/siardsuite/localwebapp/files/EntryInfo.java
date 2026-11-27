package ch.admin.bar.siardsuite.localwebapp.files;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/** One file or folder looked up by path. */
@Schema(description = "A file or folder looked up by its path")
public record EntryInfo(
        @Schema(required = true, description = "Absolute, normalised path") String path,
        @Schema(required = true) String name,
        @Schema(description = "Parent folder; null at a root") String parent,
        @Schema(required = true) boolean folder,
        @Schema(description = "Size in bytes; null for folders") Long size,
        @Schema(required = true, description = "Last modification, ISO-8601 instant") String modified,
        @Schema(required = true) boolean hidden,
        @Schema(required = true, description = "Whether the user may write the file, or create files in the folder")
        boolean writable) {
}
