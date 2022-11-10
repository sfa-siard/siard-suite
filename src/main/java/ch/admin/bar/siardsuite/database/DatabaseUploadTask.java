package ch.admin.bar.siardsuite.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.MetaData;
import ch.admin.bar.siard2.cmd.MetaDataToDb;
import ch.admin.bar.siard2.cmd.PrimaryDataToDb;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.visitor.ArchiveVisitor;
import ch.enterag.utils.background.Progress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.sql.Connection;

public class DatabaseUploadTask extends Task<ObservableList<String>> implements Progress, ArchiveVisitor {

  private final Connection connection;
  private final Model model;
  private MetaData metaData;
  private Archive archive;

  public DatabaseUploadTask(Connection connection, Model model) {
    this.connection = connection;
    this.model = model;
  }

  @Override
  public boolean cancelRequested() {
    return false;
  }

  @Override
  public void notifyProgress(int i) {

  }

  @Override
  protected ObservableList<String> call() throws Exception {
    this.model.provideArchiveProperties(this);
    ObservableList<String> progressData = FXCollections.observableArrayList();
    connection.setAutoCommit(false);

    boolean isOverwrite = true;
    boolean metaDataOnly = false;
    MetaDataToDb mdtd = MetaDataToDb.newInstance(connection.getMetaData(), this.metaData, model.getSchemaMap());
    if (isOverwrite || ((mdtd.tablesDroppedByUpload() == 0) && (mdtd.typesDroppedByUpload() == 0))) {
      mdtd.upload(this);
      if (!metaDataOnly) {
        this.model.provideArchiveObject(this);
        updateProgress(0, 100);
        PrimaryDataToDb pdtd = PrimaryDataToDb.newInstance(connection, this.archive,
                mdtd.getArchiveMapping(), mdtd.supportsArrays(), mdtd.supportsDistincts(), mdtd.supportsUdts());
        pdtd.upload(this);
      }
    }
    return progressData;
  }


  @Override
  public void visit(Archive archive) {
    this.archive = archive;
  }

  @Override
  public void visit(MetaData metaData) {
    this.metaData = metaData;
  }
}
