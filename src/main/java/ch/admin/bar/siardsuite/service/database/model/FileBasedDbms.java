package ch.admin.bar.siardsuite.service.database.model;

import ch.admin.bar.siardsuite.util.ThrowingFunction;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.io.File;
import java.util.function.Function;

@Value
@Builder
public class FileBasedDbms implements Dbms<FileBasedDbmsConnectionProperties> {
    @NonNull String name;
    @NonNull String id;
    @NonNull String driverClassName;
    @NonNull Function<FileBasedDbmsConnectionProperties, String> jdbcConnectionStringEncoder;
    @NonNull ThrowingFunction<String, FileBasedDbmsConnectionProperties> jdbcConnectionStringDecoder;

    @NonNull File exampleFile;
}
