package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ArchiveConnectionPresenter extends StepperPresenter {

  @FXML
  public Label errorMessage;
  @FXML
  public Text title;
  @FXML
  public Text text1;
  @FXML
  public Text text3;
  @FXML
  public VBox leftVBox;
  @FXML
  public VBox rightVBox;
  @FXML
  public TextFlow textFlow;
  @FXML
  public Label subtitleLeft;
  @FXML
  public Label subtitleRight;
  @FXML
  public Text textLeft;
  @FXML
  public Text textRight;
  @FXML
  public MFXTextField dbServerField;
  @FXML
  public MFXTextField dbNameField;
  @FXML
  public MFXTextField usernameField;
  @FXML
  public MFXTextField urlField;
  @FXML
  public MFXPasswordField passwordField;
  @FXML
  public MFXTextField portField;
  @FXML
  public MFXTextField connectionName;
  @FXML
  public MFXToggleButton toggleSave;
  @FXML
  public BorderPane borderPane;
  @FXML
  private MFXButton nextButton;
  @FXML
  private MFXButton previousButton;
  @FXML
  private MFXButton cancelButton;
  @FXML
  private HBox buttonsBox;

  @Override
  public void init(Controller controller, Model model, RootStage stage) {
    this.model = model;
    this.controller = controller;
    this.stage = stage;
  }

  @Override
  public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) {
    this.model = model;
    this.controller = controller;
    this.stage = stage;

    addTextWithStyles();
    addFormText();
    this.errorMessage.setVisible(false);
    this.nextButton = new MFXButton();
    this.nextButton.textProperty().bind(I18n.createStringBinding("button.next"));
    this.nextButton.getStyleClass().setAll("button", "primary");
    this.nextButton.setManaged(true);
    this.previousButton = new MFXButton();
    this.previousButton.textProperty().bind(I18n.createStringBinding("button.back"));
    this.previousButton.getStyleClass().setAll("button", "secondary");
    this.previousButton.setManaged(true);
    this.cancelButton = new MFXButton();
    this.cancelButton.textProperty().bind(I18n.createStringBinding("button.cancel"));
    this.cancelButton.getStyleClass().setAll("button", "secondary");
    this.cancelButton.setManaged(true);

    this.buttonsBox.getChildren().addAll(this.previousButton, this.cancelButton, this.nextButton);
    this.setListeners(stepper);
  }

  private void addTextWithStyles() {
    this.title.textProperty().bind(I18n.createStringBinding("archiveConnection.view.title"));
    this.subtitleLeft.textProperty().bind(I18n.createStringBinding("archiveConnection.view.subtitleLeft"));
    this.subtitleRight.textProperty().bind(I18n.createStringBinding("archiveConnection.view.subtitleRight"));
    this.textLeft.textProperty().bind(I18n.createStringBinding("archiveConnection.view.textLeft"));
    this.textRight.textProperty().bind(I18n.createStringBinding("archiveConnection.view.textRight"));
    this.errorMessage.textProperty().bind(I18n.createStringBinding("archiveConnection.view.error"));

    for (int i = 0; i < textFlow.getChildren().size(); i++) {
      Text text = (Text) textFlow.getChildren().get(i);
      text.textProperty().bind(I18n.createStringBinding("archiveConnection.view.text" + i));
    }
    text1.getStyleClass().add("bold");
    text3.getStyleClass().add("bold");
  }

  private void addFormText() {
    dbServerField.floatingTextProperty().bind(I18n.createStringBinding("archiveConnection.view.dbServer.label"));
    dbServerField.promptTextProperty().bind(I18n.createStringBinding("archiveConnection.view.dbServer.prompt"));
    portField.floatingTextProperty().bind(I18n.createStringBinding("archiveConnection.view.port.label"));
    portField.setText("1433");
    portField.setPromptText("1433");
    dbNameField.floatingTextProperty().bind(I18n.createStringBinding("archiveConnection.view.dbName.label"));
    usernameField.floatingTextProperty().bind(I18n.createStringBinding("archiveConnection.view.username.label"));
    passwordField.floatingTextProperty().bind(I18n.createStringBinding("archiveConnection.view.password.label"));
    urlField.floatingTextProperty().bind(I18n.createStringBinding("archiveConnection.view.url.label"));
//    urlField.setPromptText("jdbc:" + model.getDatabaseDriver() + "://dbserver.organisation.org:1433;databaseName=test-db");
    toggleSave.textProperty().bind(I18n.createStringBinding("archiveConnection.view.toggleSave"));
    connectionName.floatingTextProperty().bind(I18n.createStringBinding("archiveConnection.view.connectionName.label"));
  }



  private void setListeners(MFXStepper stepper) {


    toggleSave.setOnAction(event -> {
      this.connectionName.setVisible(!this.connectionName.isVisible());
    });

    this.nextButton.setOnAction((event) -> {
      if (toggleSave.isSelected() && connectionName.getText().isEmpty()) {
        this.errorMessage.setVisible(true);
      } else {
        //        controller.setDatabaseType(selected.getText());
//        this.errorMessage.setVisible(false);
//        stepper.next();
      }

    });
    this.previousButton.setOnAction((event) -> {
      this.stage.setHeight(700.00);
      stepper.previous();
    });
    this.cancelButton.setOnAction((event) -> {
      // AbortDialog
    });
  }
}
