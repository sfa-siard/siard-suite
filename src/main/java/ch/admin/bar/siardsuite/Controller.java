package ch.admin.bar.siardsuite;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.model.database.SiardArchive;
import ch.admin.bar.siardsuite.util.preferences.RecentDbConnection;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Optional;

public class Controller {

    private SiardArchive siardArchive = new SiardArchive();

    @Setter
    @Getter
    @NonNull
    private Optional<RecentDbConnection> recentDatabaseConnection = Optional.empty();

    @Setter
    @Getter
    @NonNull
    private Optional<DbmsConnectionData> databaseConnectionData = Optional.empty();

    public void setSiardArchive(String name, Archive archive) {
        setSiardArchive(name, archive, false);
    }

    public void setSiardArchive(String name, Archive archive, boolean onlyMetaData) {
        this.siardArchive = new SiardArchive(name, archive, onlyMetaData);
    }

    public SiardArchive getSiardArchive() {
        if (this.siardArchive == null) this.siardArchive = new SiardArchive();
        return siardArchive;
    }

    public void clearSiardArchive() {
        this.siardArchive = new SiardArchive();
    }
}
