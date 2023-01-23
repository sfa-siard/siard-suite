package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.model.database.*;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import ch.admin.bar.siardsuite.visitor.SiardArchiveMetaDataVisitor;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class ContentRootPresenter extends TreePresenter implements SiardArchiveVisitor, SiardArchiveMetaDataVisitor {

  @FXML
  public VBox container;

  @FXML
  private Label textFormat;
  @FXML
  private Label textDb;
  @FXML
  public Label textProduct;
  @FXML
  public Label textConnection;
  @FXML
  public Label textUsername;
  @FXML
  public Label textDesc;
  @FXML
  public Label textOwner;
  @FXML
  public Label textDataOriginTimespan;
  @FXML
  public Label textArchiveDate;
  @FXML
  public Label textArchiveUser;
  @FXML
  public Label textContactArchiveUser;

  @Override
  public void init(Controller controller, Model model, RootStage stage, TreeAttributeWrapper wrapper) {
    this.model = model;
    this.controller = controller;
    this.stage = stage;

    initLabels();

    this.model.provideDatabaseArchiveMetaDataProperties(this);
    this.model.provideDatabaseArchiveProperties(this);

    setListeners();
  }

  private void initLabels() {
    for (Node child : this.container.getChildren()) {
      if (child.getId().contains("label")) {
        ((Label) child).textProperty().bind(I18n.createStringBinding("tableContainer." + child.getId()));
      }
    }
  }

  protected void setListeners() {

  }

  @Override
  public void visit(String archiveName, boolean onlyMetaData, List<DatabaseSchema> schemas) {}

  @Override
  public void visit(String schemaName, String schemaDescription, List<DatabaseTable> tables) {}

  @Override
  public void visit(String tableName, String numberOfRows, List<DatabaseColumn> columns, List<DatabaseRow> rows) {}

  @Override
  public void visit(String columnName) {}

  @Override
  public void visit(SiardArchive archive) {}

  @Override
  public void visit(String siardFormatVersion, String databaseName, String databaseProduct, String databaseConnectionURL,
                    String databaseUsername, String databaseDescription, String databaseOwner, String databaseCreationDate,
                    LocalDate archivingDate, String archiverName, String archiverContact, File targetArchive) {
    textFormat.setText(siardFormatVersion);
    textDb.setText(databaseName);
    textProduct.setText(databaseProduct);
    textConnection.setText(databaseConnectionURL);
    textUsername.setText(databaseUsername);
    textDesc.setText(databaseDescription);
    textOwner.setText(databaseOwner);
    textDataOriginTimespan.setText(databaseCreationDate);
    textArchiveDate.textProperty().bind(Bindings.createStringBinding(() -> I18n.getLocaleDate(archivingDate), I18n.localeProperty()));
    textArchiveUser.setText(archiverName);
    textContactArchiveUser.setText(archiverContact);
  }

  @Override
  public void visit(SiardArchiveMetaData metaData) {}

}
