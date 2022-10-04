package ch.admin.bar.siardsuite.model.service;

import ch.admin.bar.siardsuite.model.DataTable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class DatabaseLoadTask extends Task<ObservableList<DataTable>> {
  @Override
  protected ObservableList<DataTable> call() throws Exception {

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
}
