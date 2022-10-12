package ch.admin.bar.siardsuite.db;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.Schema;
import ch.admin.bar.siard2.cmd.MetaDataFromDb;
import ch.admin.bar.siard2.cmd.PrimaryDataFromDb;
import ch.admin.bar.siardsuite.model.DataTable;
import ch.enterag.utils.background.Progress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.sql.Connection;

public class DatabaseLoadTask extends Task<ObservableList<DataTable>> implements Progress {

  private Connection connection;
  private Archive archive;

  public DatabaseLoadTask(Connection connection, Archive archive) {
    this.connection = connection;
    this.archive = archive;
  }

  @Override
  protected ObservableList<DataTable> call() throws Exception {

    ObservableList<DataTable> progressData = FXCollections.observableArrayList();
    connection.setAutoCommit(false);
    MetaDataFromDb metadata = MetaDataFromDb.newInstance(connection.getMetaData(), archive.getMetaData());
    metadata.download(true, false, this);
    for (int i = 0; i < this.archive.getSchemas(); i++) {
      Schema schema = this.archive.getSchema(i);
      for (int y = 0; y < schema.getTables(); y++) {
        progressData.add(new DataTable(schema.getMetaSchema().getName() + "." + schema.getTable(y).getMetaTable().getName()));
      }
    }
    updateValue(progressData);
    updateProgress(0, progressData.size());

    PrimaryDataFromDb data = PrimaryDataFromDb.newInstance(connection, archive);
    // TODO PrimaryDataFromDB needs extension show progress per table - CR #459
    data.download(this);

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
