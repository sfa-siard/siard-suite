package ch.admin.bar.siardsuite.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.MetaData;
import ch.admin.bar.siard2.api.Schema;
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
    this.model.provideArchiveObject(this);
    ObservableList<String> progressData = FXCollections.observableArrayList();
    connection.setAutoCommit(false);

    // TODO overwrite and metadataonly?
    boolean isOverwrite = true;
    boolean metaDataOnly = false;

    MetaDataToDb metadata = MetaDataToDb.newInstance(connection.getMetaData(), this.metaData, model.getSchemaMap());
    if (isOverwrite || ((metadata.tablesDroppedByUpload() == 0) && (metadata.typesDroppedByUpload() == 0))) {
      metadata.upload(this);
      for (int i = 0; i < this.archive.getSchemas(); i++) {
        Schema schema = this.archive.getSchema(i);
        for (int y = 0; y < schema.getTables(); y++) {
          progressData.add(schema.getMetaSchema().getName() + "." + schema.getTable(y).getMetaTable().getName());
        }
      }
      updateValue(progressData);
      updateProgress(0, progressData.size());

      if (!metaDataOnly) {
        PrimaryDataToDb pdtd = PrimaryDataToDb.newInstance(connection, this.archive,
                metadata.getArchiveMapping(), metadata.supportsArrays(), metadata.supportsDistincts(), metadata.supportsUdts());
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
