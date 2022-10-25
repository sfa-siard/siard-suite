package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.TreeContentViewModel;
import ch.admin.bar.siardsuite.model.database.DatabaseSchema;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import ch.admin.bar.siardsuite.visitor.DatabaseArchiveMetaDataVisitor;
import ch.admin.bar.siardsuite.visitor.DatabaseArchiveVisitor;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;

public class ContentRootPresenter extends TreePresenter implements DatabaseArchiveVisitor, DatabaseArchiveMetaDataVisitor {

  @FXML
  public VBox container;
  @FXML
  private Label titleTableContainer;

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
  public Label textCreationDate;
  @FXML
  public Label textArchiveDate;
  @FXML
  public Label textArchiveUser;
  @FXML
  public Label textContactArchiveUser;

  @Override
  public void init(Controller controller, TreeContentViewModel model, RootStage stage) {
    this.model = model.getModel();
    this.controller = controller;
    this.stage = stage;

    this.titleTableContainer.textProperty().bind(I18n.createStringBinding(model.getTitle()));

    initLabels();

    controller.provideDatabaseArchiveMetaData(this);
    controller.provideDatabaseArchive(this);
  }

  private void initLabels() {
    for (Node child : this.container.getChildren()) {
      if (child.getId().contains("label")) {
        ((Label) child).textProperty().bind(I18n.createStringBinding("tableContainer." + child.getId()));
      }
    }
  }

  @Override
  public void visit(String archiveName, List<DatabaseSchema> schemas) {}

  @Override
  public void visit(String siardFormatVersion, String databaseName, String databaseProduct, String databaseConnectionURL,
                    String databaseUsername, String databaseDescription, String databaseOwner, String databaseCreationDate,
                    String archivingDate, String archiverName, String archiverContact) {
    textFormat.setText(siardFormatVersion);
    textDb.setText(databaseName);
    textProduct.setText(databaseProduct);
    textConnection.setText(databaseConnectionURL);
    textUsername.setText(databaseUsername);
    textDesc.setText(databaseDescription);
    textOwner.setText(databaseOwner);
    textCreationDate.setText(databaseCreationDate);
    textArchiveDate.setText(archivingDate);
    textArchiveUser.setText(archiverName);
    textContactArchiveUser.setText(archiverContact);

  }

}
