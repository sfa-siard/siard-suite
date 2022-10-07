package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.StepperButtonBox;
import ch.admin.bar.siardsuite.model.DatabaseConnectionProperties;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.*;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import static ch.admin.bar.siardsuite.model.DatabaseConnectionProperties.*;

public class ArchiveConnectionPresenter extends StepperPresenter {

  private static final String DBSERVER_ORGANISATION_ORG = "dbserver.organisation.org";
  private static final String TEST_DB = "test-db";
  private static final String MY_USER = "MyUser";

  private String portString;
  private String dbTypeString;
  @FXML
  public Label errorMessage;
  @FXML
  public Text title;
  @FXML
  public Text text1;
  @FXML
  public Text text3;
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
  private StepperButtonBox buttonsBox;
  @FXML
  public MFXButton infoButton;
  @FXML
  private Tooltip tooltip;

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
    makeTooltip();

    this.errorMessage.setVisible(false);
    this.buttonsBox = new StepperButtonBox().make(StepperButtonBox.DEFAULT);
    this.borderPane.setBottom(buttonsBox);
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
    dbNameField.floatingTextProperty().bind(I18n.createStringBinding("archiveConnection.view.dbName.label"));
    usernameField.floatingTextProperty().bind(I18n.createStringBinding("archiveConnection.view.username.label"));
    passwordField.floatingTextProperty().bind(I18n.createStringBinding("archiveConnection.view.password.label"));
    urlField.floatingTextProperty().bind(I18n.createStringBinding("archiveConnection.view.url.label"));
    toggleSave.textProperty().bind(I18n.createStringBinding("archiveConnection.view.toggleSave"));
    connectionName.floatingTextProperty().bind(I18n.createStringBinding("archiveConnection.view.connectionName.label"));
  }

  private void makeTooltip() {
    tooltip = new Tooltip();
    tooltip.setPrefSize(328.0, 162);
    tooltip.setShowDelay(Duration.millis(1));
    tooltip.setAutoHide(true);
    tooltip.getStyleClass().add("info-tooltip");
    tooltip.textProperty().bind(I18n.createStringBinding("archiveConnection.view.tooltip"));
  }

  private void setListeners(MFXStepper stepper) {
    stepper.addEventHandler(SiardEvent.UPDATE_STEPPER_DBTYPE_EVENT, event -> {
      DatabaseConnectionProperties.DatabaseProperties props = model.getDatabaseProps();
      this.dbTypeString = props.defaultUrl().replace(PRODUCT, props.product());
      this.portString = props.port();
      String url = this.dbTypeString
              .replace(HOST, DBSERVER_ORGANISATION_ORG)
              .replace(PORT, portString)
              .replace(DB_NAME, TEST_DB );

      portField.setText(portString);
      portField.setPromptText(portString);

      urlField.setPromptText(url);
    });

    dbServerField.setOnKeyReleased(this::handleKeyEvent);
    dbNameField.setOnKeyReleased(this::handleKeyEvent);
    portField.setOnKeyReleased(this::handleKeyEvent);
    usernameField.setOnKeyReleased(this::handleKeyEvent);

    toggleSave.setOnAction(event -> this.connectionName.setVisible(!this.connectionName.isVisible()));

    infoButton.setOnMouseMoved(event -> {
      Bounds boundsInScreen = infoButton.localToScreen(infoButton.getBoundsInLocal());
      tooltip.show(infoButton,
              (boundsInScreen.getMaxX() - boundsInScreen.getWidth() / 2) - tooltip.getWidth() / 2,
              boundsInScreen.getMaxY() - boundsInScreen.getHeight() - tooltip.getHeight());
    });

    infoButton.setOnMouseExited(event -> tooltip.hide());

    this.buttonsBox.next().setOnAction((event) -> {
      if (toggleSave.isSelected() && connectionName.getText().isEmpty()) {
        this.errorMessage.setVisible(true);
      } else {
        controller.updateConnectionData(urlField.getText(), this.usernameField.getText(), this.dbNameField.getText(), this.passwordField.getText());
        this.errorMessage.setVisible(false);
        this.stage.setHeight(950);
        stepper.next();
        stepper.fireEvent(getUpdateEvent(SiardEvent.UPDATE_STEPPER_DBLOAD_EVENT));
      }

    });
    this.buttonsBox.previous().setOnAction((event) -> {
      stepper.previous();
      this.stage.setHeight(700.00);
    });
    this.buttonsBox.cancel().setOnAction((event) -> stage.openDialog(View.ARCHIVE_ABORT_DIALOG.getName()));
  }

  private void handleKeyEvent(KeyEvent event) {
    String inputText = event.getText();
    if (inputText != null) {
      String server = dbServerField.getText().isEmpty() ? DBSERVER_ORGANISATION_ORG : dbServerField.getText();
      String dbname = dbNameField.getText().isEmpty() ? TEST_DB : dbNameField.getText();
      String port = portField.getText().isEmpty() ? portString : portField.getText();

      String url = dbTypeString
              .replace(HOST, server)
              .replace(PORT, port)
              .replace(DB_NAME, dbname);

      urlField.setText(url);
    }
    event.consume();
  }
}
