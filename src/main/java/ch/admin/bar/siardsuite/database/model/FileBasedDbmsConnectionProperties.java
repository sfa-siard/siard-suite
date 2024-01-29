package ch.admin.bar.siardsuite.database.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.io.File;

@Value
@Builder
@RequiredArgsConstructor
public class FileBasedDbmsConnectionProperties implements DbmsConnectionProperties<FileBasedDbmsConnectionProperties> {
    @NonNull File file;
}
