package ch.admin.bar.siardsuite.service.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.cmd.MetaDataToDb;
import ch.admin.bar.siard2.cmd.PrimaryDataToDb;
import ch.admin.bar.siardsuite.service.preferences.UserPreferences;
import ch.enterag.utils.background.Progress;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.sql.Connection;
import java.util.Map;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class DatabaseUploadTask extends Task<String> implements Progress {

  private final Supplier<Connection> connectionSupplier;
  private final Archive archive;
  private final Map<String, String> schemaNameMapping;

  @Override
  public boolean cancelRequested() {
    return false;
  }

  @Override
  public void notifyProgress(int i) {

  }

  @Override
  protected String call() throws Exception {
    val connection = connectionSupplier.get();

    int timeout = UserPreferences.INSTANCE.getStoredOptions().getQueryTimeout();

    // TODO overwrite and metadataonly?
    boolean isOverwrite = true;
    boolean metaDataOnly = false;

    MetaDataToDb metadata = MetaDataToDb.newInstance(connection.getMetaData(), this.archive.getMetaData(), this.schemaNameMapping);
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
}