package ch.admin.bar.siardsuite.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.Schema;
import ch.admin.bar.siard2.cmd.MetaDataFromDb;
import ch.admin.bar.siard2.cmd.PrimaryDataFromDb;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.database.DatabaseTable;
import ch.enterag.utils.background.Progress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.sql.Connection;

public class DatabaseLoadTask extends Task<ObservableList<DatabaseTable>> implements Progress {

  private Connection connection;
  private Model model;
  private Archive archive;

  public DatabaseLoadTask(Connection connection, Model model, Archive archive) {
    this.connection = connection;
    this.model = model;
    this.archive = archive;
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
        progressData.add(new DatabaseTable(schema.getMetaSchema().getName() + "." + schema.getTable(y).getMetaTable().getName()));
      }
    }
    updateValue(progressData);
    updateProgress(0, progressData.size());

//    PrimaryDataFromDb data = PrimaryDataFromDb.newInstance(connection, archive);
    // TODO PrimaryDataFromDB needs extension show progress per table - CR #459
//    data.download(this);

    // for test purposes:
    model.setArchive("sample.siard", archive);

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