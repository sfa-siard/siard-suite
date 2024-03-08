package ch.admin.bar.siardsuite.presenter.upload.model;

import ch.admin.bar.siardsuite.database.model.DbmsConnectionData;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class ConnectionDataWithSchemaNameMapping {
    @NonNull DbmsConnectionData connectionData;
    @NonNull Map<String, String> schemaNameMapping;
}
