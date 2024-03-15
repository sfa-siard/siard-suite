package ch.admin.bar.siardsuite.presenter.upload.model;

import ch.admin.bar.siardsuite.service.database.model.DbmsConnectionData;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class UploadArchiveData {
    @NonNull DbmsConnectionData connectionData;
    @NonNull Map<String, String> schemaNameMappings;
}
