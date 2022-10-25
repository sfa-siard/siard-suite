package ch.admin.bar.siardsuite.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.Schema;
import ch.admin.bar.siard2.cmd.MetaDataFromDb;
import ch.admin.bar.siard2.cmd.PrimaryDataFromDb;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.database.DatabaseArchiveMetaData;
import ch.admin.bar.siardsuite.model.database.DatabaseTable;
import ch.admin.bar.siardsuite.visitor.DatabaseArchiveMetaDataVisitor;
import ch.enterag.utils.background.Progress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.File;
import java.sql.Connection;

public class DatabaseLoadTask extends Task<ObservableList<DatabaseTable>> implements Progress, DatabaseArchiveMetaDataVisitor {

    private final Connection connection;
    private final Model model;
    private final Archive archive;
    private DatabaseArchiveMetaData metaData;
    private final boolean onlyMetaData;

    public DatabaseLoadTask(Connection connection, Model model, Archive archive, boolean onlyMetaData) {
        this.connection = connection;
        this.model = model;
        this.archive = archive;
        this.onlyMetaData = onlyMetaData;
    }

    @Override
    protected ObservableList<DatabaseTable> call() throws Exception {

        ObservableList<DatabaseTable> progressData = FXCollections.observableArrayList();
        connection.setAutoCommit(false);
        MetaDataFromDb metadata = MetaDataFromDb.newInstance(connection.getMetaData(), archive.getMetaData());
        metadata.download(true, false, this);
        for (int i = 0; i < this.archive.getSchemas(); i++) {
            Schema schema = this.archive.getSchema(i);
            for (int y = 0; y < schema.getTables(); y++) {
                progressData.add(new DatabaseTable(schema.getMetaSchema().getName() + "." + schema.getTable(y)
                                                                                                  .getMetaTable()
                                                                                                  .getName()));
            }
        }

        updateValue(progressData);
        updateProgress(0, progressData.size());

        model.provideDatabaseArchiveMetaDataObject(this);
        metaData.write(archive);

        // TODO: replace the boolean flag with a strategy pattern
        if (!onlyMetaData) {
            // TODO PrimaryDataFromDB needs extension show progress per table -CR #459
            PrimaryDataFromDb data = PrimaryDataFromDb.newInstance(connection, archive);
            data.download(this);
        }

        model.setArchive("sample.siard", archive);  //TODO: there should probably be different tasks for the preview and the full download
        archive.close(); // maybe this is needed - but here?
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
    public void visit(String siardFormatVersion, String databaseName, String databaseProduct, String databaseConnectionURL,
                      String databaseUsername, String databaseDescription, String databaseOwner, String databaseCreationDate,
                      String archivingDate, String archiverName, String archiverContact, File targetArchive) {}

    @Override
    public void visit(DatabaseArchiveMetaData metaData) {
        this.metaData = metaData;
    }

}
