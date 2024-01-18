package ch.admin.bar.siardsuite.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.Schema;
import ch.admin.bar.siard2.cmd.MetaDataFromDb;
import ch.admin.bar.siard2.cmd.PrimaryDataFromDb;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.database.SiardArchiveMetaData;
import ch.admin.bar.siardsuite.util.preferences.UserPreferences;
import ch.admin.bar.siardsuite.visitor.SiardArchiveMetaDataVisitor;
import ch.enterag.utils.background.Progress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.util.Pair;

import java.sql.Connection;

import static ch.admin.bar.siardsuite.util.preferences.UserPreferences.KeyIndex.QUERY_TIMEOUT;
import static ch.admin.bar.siardsuite.util.preferences.UserPreferences.NodePath.OPTIONS;

public class DatabaseLoadTask extends Task<ObservableList<Pair<String, Long>>> implements Progress, SiardArchiveMetaDataVisitor {

    private final Connection connection;
    private final Model model;
    private final Archive archive;
    private SiardArchiveMetaData metaData;
    private final boolean onlyMetaData;
    private boolean viewsAsTables;
    private String name;

    public DatabaseLoadTask(Connection connection, Model model, Archive archive, boolean onlyMetaData,
                            boolean viewsAsTables) {
        this.connection = connection;
        this.model = model;
        this.archive = archive;
        this.onlyMetaData = onlyMetaData;
        this.viewsAsTables = viewsAsTables;
    }

    @Override
    protected ObservableList<Pair<String, Long>> call() throws Exception {

        ObservableList<Pair<String, Long>> progressData = FXCollections.observableArrayList();
        connection.setAutoCommit(false);
        int timeout = Integer.parseInt(UserPreferences.node(OPTIONS).get(QUERY_TIMEOUT.name(), "0"));

        archive.getMetaData().setDbName(model.getDatabaseName().getValue());

        MetaDataFromDb metadata = MetaDataFromDb.newInstance(connection.getMetaData(), archive.getMetaData());
        metadata.setQueryTimeout(timeout);
        updateValue(FXCollections.observableArrayList(new Pair<>("Metadata", -1L)));
        updateProgress(0, 100);
        metadata.download(viewsAsTables, false, this);

        model.provideDatabaseArchiveMetaDataObject(this); // very complicated getter for the SiardArchiveMetaData
        if (metaData != null) {
            metaData.write(archive);
        }

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

        model.setSiardArchive(name, archive, onlyMetaData);
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

    @Override
    public void visit(SiardArchiveMetaData metaData) {
        this.metaData = metaData;
    }

}
