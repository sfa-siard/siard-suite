package ch.admin.bar.siardsuite.localwebapp.files;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import ch.admin.bar.siardsuite.webcore.error.ProblemException;

/**
 * Parses the absolute paths clients send (design D2). Every malformed path is a 400; nothing here
 * touches the file system.
 */
final class FilePaths {

    static final String PATH_REQUIRED = "An absolute path is required";

    private FilePaths() {
    }

    /** The absolute, normalised path, or a 400 problem for empty, relative or unparsable input. */
    static Path parse(String raw) {
        if (raw == null || raw.isBlank() || raw.indexOf('\0') >= 0) {
            throw new ProblemException(400, PATH_REQUIRED);
        }
        Path path;
        try {
            path = Path.of(raw);
        } catch (InvalidPathException e) {
            throw new ProblemException(400, PATH_REQUIRED);
        }
        if (!path.isAbsolute()) {
            throw new ProblemException(400, PATH_REQUIRED);
        }
        return path.normalize();
    }

    /** The parent as a string, or null at a root. */
    static String parentOf(Path path) {
        Path parent = path.getParent();
        return parent == null ? null : parent.toString();
    }
}
