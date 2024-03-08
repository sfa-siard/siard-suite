package ch.admin.bar.siardsuite.presenter.archive.model;

import ch.admin.bar.siardsuite.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.model.database.SiardArchive;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class SiardArchiveWithConnectionData {
    @NonNull SiardArchive siardArchive;
    @NonNull DbmsConnectionData dbmsConnectionData;
}
