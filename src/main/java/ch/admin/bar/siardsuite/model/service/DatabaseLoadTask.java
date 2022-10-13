package ch.admin.bar.siardsuite.model.service;

import ch.admin.bar.siardsuite.model.database.DatabaseTable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class DatabaseLoadTask extends Task<ObservableList<DatabaseTable>> {
  @Override
  protected ObservableList<DatabaseTable> call() throws Exception {

    // TODO buildup Model with Database-Data
    // Metadata, Schema, Tables and Columns -> no

    ObservableList<DatabaseTable> data = FXCollections.observableArrayList();
    data.add(new DatabaseTable("BONUS"));
    data.add(new DatabaseTable("DEPT"));
    data.add(new DatabaseTable("EMP"));
    data.add(new DatabaseTable("SALGRADE"));
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
}
