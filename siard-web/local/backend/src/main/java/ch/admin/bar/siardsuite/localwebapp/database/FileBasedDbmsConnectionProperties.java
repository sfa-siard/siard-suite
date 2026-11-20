package ch.admin.bar.siardsuite.localwebapp.database;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

/** Connection properties of a file-based DBMS such as MS Access. */
public record FileBasedDbmsConnectionProperties(Path file) implements DbmsConnectionProperties {

    public FileBasedDbmsConnectionProperties {
        requireNonNull(file, "file");
    }
}
