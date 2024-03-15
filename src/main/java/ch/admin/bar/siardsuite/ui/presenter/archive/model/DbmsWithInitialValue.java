package ch.admin.bar.siardsuite.ui.presenter.archive.model;

import ch.admin.bar.siardsuite.service.database.model.Dbms;
import ch.admin.bar.siardsuite.service.preferences.RecentDbConnection;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;

@Builder
@Value
public class DbmsWithInitialValue {
    @NonNull
    Dbms dbms;

    @NonNull
    @Builder.Default
    Optional<RecentDbConnection> initialValue = Optional.empty();
}
