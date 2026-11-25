package ch.admin.bar.siardsuite.localwebapp.files;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/** The entry to show in the system file manager. */
@Schema(description = "The entry to show in the system file manager")
public record RevealRequest(@Schema(required = true, description = "Absolute path") String path) {
}
