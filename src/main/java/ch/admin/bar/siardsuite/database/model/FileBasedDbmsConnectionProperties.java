package ch.admin.bar.siardsuite.database.model;

import lombok.NonNull;
import lombok.Value;

import java.io.File;

@Value
public class FileBasedDbmsConnectionProperties implements DbmsConnectionProperties<FileBasedDbmsConnectionProperties> {
    @NonNull File file;
}
