package ch.admin.bar.siardsuite.localwebapp.files;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/** A starting place of the file chooser. */
@Schema(description = "A starting place of the file chooser")
public record Place(
        @Schema(required = true, enumeration = {"home", "desktop", "documents", "downloads", "root"}) String kind,
        @Schema(required = true, description = "Absolute path") String path) {

    static final String HOME = "home";
    static final String DESKTOP = "desktop";
    static final String DOCUMENTS = "documents";
    static final String DOWNLOADS = "downloads";
    static final String ROOT = "root";
}
