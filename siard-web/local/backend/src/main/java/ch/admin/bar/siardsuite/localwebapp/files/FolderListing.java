package ch.admin.bar.siardsuite.localwebapp.files;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/** A folder's entries after filtering: folders first, then files, each by name ignoring case. */
@Schema(description = "The entries of a folder")
public record FolderListing(
        @Schema(required = true, description = "Absolute, normalised path of the folder") String path,
        @Schema(description = "Parent folder; null at a root") String parent,
        @Schema(required = true) List<FileEntry> entries,
        @Schema(required = true, description = "True when entries beyond the limit were left out") boolean truncated) {
}
