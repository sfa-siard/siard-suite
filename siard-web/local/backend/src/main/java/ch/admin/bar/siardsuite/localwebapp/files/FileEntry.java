package ch.admin.bar.siardsuite.localwebapp.files;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/** One entry of a folder listing. */
@Schema(description = "A file or folder in a listing")
public record FileEntry(
        @Schema(required = true) String name,
        @Schema(required = true, description = "Absolute path") String path,
        @Schema(required = true) boolean folder,
        @Schema(description = "Size in bytes; null for folders") Long size,
        @Schema(required = true, description = "Last modification, ISO-8601 instant") String modified,
        @Schema(required = true) boolean hidden) {
}
