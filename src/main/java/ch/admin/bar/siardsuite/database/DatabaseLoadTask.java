package ch.admin.bar.siardsuite.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.Schema;
import ch.admin.bar.siard2.cmd.MetaDataFromDb;
import ch.admin.bar.siard2.cmd.PrimaryDataFromDb;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.util.preferences.UserPreferences;
import ch.enterag.utils.background.Progress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.util.Pair;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.sql.Connection;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class DatabaseLoadTask extends Task<ObservableList<Pair<String, Long>>> implements Progress {

    private final Supplier<Connection> connectionSupplier;
    private final Model model;
    private final String dbName;
    private final Archive archive;
    private final boolean onlyMetaData;
    private final boolean viewsAsTables;

    @Override
    protected ObservableList<Pair<String, Long>> call() throws Exception {
        val connection = connectionSupplier.get();

        ObservableList<Pair<String, Long>> progressData = FXCollections.observableArrayList();
        int timeout = UserPreferences.INSTANCE.getStoredOptions().getQueryTimeout();

        archive.getMetaData().setDbName(dbName);

        MetaDataFromDb metaDataFromDb = MetaDataFromDb.newInstance(connection.getMetaData(), archive.getMetaData());
        metaDataFromDb.setQueryTimeout(timeout);

        updateValue(FXCollections.observableArrayList(new Pair<>("Metadata", -1L)));
        updateProgress(0, 100);

        metaDataFromDb.download(viewsAsTables, false, this);

        // store user defined metadata to archive (if present)
        model.provideDatabaseArchiveMetaDataObject(metaData -> {
            if (metaData != null) {
                metaData.write(archive);
            }
        });


        // TODO: replace the boolean flag with a strategy pattern
        if (!onlyMetaData) {
            PrimaryDataFromDb data = PrimaryDataFromDb.newInstance(connection, archive);
            data.setQueryTimeout(timeout);
            updateValue(FXCollections.observableArrayList(new Pair<>("Dataload", -1L)));
            updateProgress(0, 100);
            data.download(this);

            for (int i = 0; i < this.archive.getSchemas(); i++) {
                Schema schema = this.archive.getSchema(i);
                for (int y = 0; y < schema.getTables(); y++) {
                    progressData.add(new Pair<>(schema.getMetaSchema().getName() + "." + schema.getTable(y)
                                                                                               .getMetaTable()
                                                                                               .getName(),
                                                schema.getTable(y).getMetaTable().getRows()));
                }
            }
            updateValue(progressData);
        }

        model.setSiardArchive(null, archive, onlyMetaData);
        // Closing is mandatory to write the archive to the filesystem
        archive.close();
        return progressData;
    }

    @Override
    public boolean cancelRequested() {
        return false;
    }

    @Override
    public void notifyProgress(int i) {
        updateProgress(i, 100);
    }
}
