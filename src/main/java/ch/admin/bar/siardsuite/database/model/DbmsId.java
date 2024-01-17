package ch.admin.bar.siardsuite.database.model;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class DbmsId {
    @NonNull String value;
}
