package ch.admin.bar.siardsuite.model.service;

import ch.admin.bar.siard2.api.Archive;
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

    MetaDataFromDb mdfd = MetaDataFromDb.newInstance(connection.getMetaData(), archive.getMetaData());
    mdfd.download(true, false, this);

    updateProgress(0,100);
    PrimaryDataFromDb pdfd = PrimaryDataFromDb.newInstance(connection, archive);
    pdfd.download(this);
    // TODO buildup Model with Database-Data
    // Metadata, Schema, Tables and Columns -> no

    ObservableList<DataTable> data = FXCollections.observableArrayList();
    data.add(new DataTable("BONUS"));
    data.add(new DataTable("DEPT"));
    data.add(new DataTable("EMP"));
    data.add(new DataTable("SALGRADE"));
    updateValue(data);

    updateProgress(1,4);
    Thread.sleep(1505);

    updateProgress(2,4);
    Thread.sleep(1505);

    updateProgress(3,4);
    Thread.sleep(1505);

    updateProgress(4,4);


    return data;
  }

  @Override
  public boolean cancelRequested() {
    return false;
  }

  @Override
  public void notifyProgress(int i) {
    updateProgress(i,100);
  }
}
