package ch.admin.bar.siardsuite.presenter.upload.model;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.database.model.Dbms;
import ch.admin.bar.siardsuite.util.preferences.RecentDbConnection;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;

@Builder
@Value
public class EditConnecionPropertiesData {
    @NonNull
    Archive archive;

    @NonNull
    Dbms dbms;

    @NonNull
    @Builder.Default
    Optional<RecentDbConnection> initialValue = Optional.empty();
}
