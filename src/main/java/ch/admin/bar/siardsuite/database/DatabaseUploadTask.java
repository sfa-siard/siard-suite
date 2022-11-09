package ch.admin.bar.siardsuite.database;

import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.database.SiardArchiveMetaData;
import ch.admin.bar.siardsuite.visitor.SiardArchiveMetaDataVisitor;
import ch.enterag.utils.background.Progress;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.File;
import java.sql.Connection;
import java.time.LocalDate;

public class DatabaseUploadTask extends Task<ObservableList<String>> implements Progress, SiardArchiveMetaDataVisitor {
  public DatabaseUploadTask(Connection connection, Model model) {
  }

  @Override
  public void visit(String siardFormatVersion, String databaseName, String databaseProduct, String databaseConnectionURL, String databaseUsername, String databaseDescription, String databseOwner, String databaseCreationDate, LocalDate archivingDate, String archiverName, String archiverContact, File targetArchive) {

  }

  @Override
  public void visit(SiardArchiveMetaData metaData) {

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
    return null;
  }
}
