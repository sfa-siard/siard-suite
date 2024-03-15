package ch.admin.bar.siardsuite;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.database.SiardArchive;
import ch.admin.bar.siardsuite.util.preferences.RecentDbConnection;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Optional;

public class Controller {

    @Getter
    private final Model model;

    @Setter
    @Getter
    @NonNull
    private Optional<RecentDbConnection> recentDatabaseConnection = Optional.empty();

    @Setter
    @Getter
    @NonNull
    private Optional<DbmsConnectionData> databaseConnectionData = Optional.empty();

    public Controller(Model model) {
        this.model = model;
    }

    public SiardArchive getSiardArchive() {
        return model.getSiardArchive();
    }

    public void setSiardArchive(String name, Archive archive) {
        this.model.setSiardArchive(name, archive);
    }
}
