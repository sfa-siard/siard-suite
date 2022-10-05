package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.TreeContentViewModel;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class ContentRootPresenter extends TreePresenter {

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

    this.textFormat.textProperty().bindBidirectional(model.getModel().getSiardFormat());
    this.textDb.textProperty().bindBidirectional(model.getModel().getDatabaseName());
    this.textProduct.textProperty().bind(model.getModel().getDatabaseProduct());
    this.textConnection.textProperty().bind(model.getModel().getConnectionUrl());
    this.textUsername.textProperty().bind(model.getModel().getDatabaseUsername());

    this.textDesc.setText("-");
    this.textOwner.setText("mysql");
    this.textCreationDate.setText("17.11.2020");
    this.textArchiveDate.setText(LocalDate.now().toString());
    this.textArchiveUser.textProperty().bind(model.getModel().getDatabaseUsername());
    this.textContactArchiveUser.setText("-");
  }

  private void initLabels() {
    for (Node child : this.container.getChildren()) {
      if (child.getId().contains("label")) {
        ((Label) child).textProperty().bind(I18n.createStringBinding("tableContainer." + child.getId()));
      }
    }
  }


}
