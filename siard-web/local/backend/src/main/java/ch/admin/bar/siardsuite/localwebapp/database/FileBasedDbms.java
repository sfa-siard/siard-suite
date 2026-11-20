package ch.admin.bar.siardsuite.localwebapp.database;

import java.nio.file.Path;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/** A DBMS stored in a single file, such as MS Access. */
public record FileBasedDbms(
        String name,
        String id,
        String driverClassName,
        Function<FileBasedDbmsConnectionProperties, String> jdbcConnectionStringEncoder,
        Function<String, FileBasedDbmsConnectionProperties> jdbcConnectionStringDecoder,
        Path exampleFile) implements Dbms<FileBasedDbmsConnectionProperties> {

    public FileBasedDbms {
        requireNonNull(name, "name");
        requireNonNull(id, "id");
        requireNonNull(driverClassName, "driverClassName");
        requireNonNull(jdbcConnectionStringEncoder, "jdbcConnectionStringEncoder");
        requireNonNull(jdbcConnectionStringDecoder, "jdbcConnectionStringDecoder");
        requireNonNull(exampleFile, "exampleFile");
    }
}
