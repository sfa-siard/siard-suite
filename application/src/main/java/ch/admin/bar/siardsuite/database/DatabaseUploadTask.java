package ch.admin.bar.siardsuite.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.MetaData;
import ch.admin.bar.siard2.cmd.MetaDataToDb;
import ch.admin.bar.siard2.cmd.PrimaryDataToDb;
import ch.admin.bar.siardsuite.util.UserPreferences;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.visitor.ArchiveVisitor;
import ch.enterag.utils.background.Progress;
import javafx.concurrent.Task;

import java.sql.Connection;

public class DatabaseUploadTask extends Task<String> implements Progress, ArchiveVisitor {

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
  protected String call() throws Exception {
    this.model.provideArchiveProperties(this);
    this.model.provideArchiveObject(this);

    connection.setAutoCommit(false);
    int timeout = Integer.parseInt(UserPreferences.node(UserPreferences.NodePath.OPTIONS).get(UserPreferences.KeyIndex.QUERY_TIMEOUT.name(), "0"));

    // TODO overwrite and metadataonly?
    boolean isOverwrite = true;
    boolean metaDataOnly = false;

    MetaDataToDb metadata = MetaDataToDb.newInstance(connection.getMetaData(), this.metaData, model.getSchemaMap());
    metadata.setQueryTimeout(timeout);
    if (!isOverwrite) {
      if ((metadata.tablesDroppedByUpload() == 0)) {
        metadata.typesDroppedByUpload();
      }
    }
    updateValue("Metadata");
    updateProgress(0, 100);
    metadata.upload(this);

    if (!metaDataOnly) {
      PrimaryDataToDb data = PrimaryDataToDb.newInstance(connection, this.archive,
              metadata.getArchiveMapping(), metadata.supportsArrays(), metadata.supportsDistincts(), metadata.supportsUdts());
      data.setQueryTimeout(timeout);
      updateValue("Dataload");
      updateProgress(0, 100);
      data.upload(this);
    }
    return null;
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
