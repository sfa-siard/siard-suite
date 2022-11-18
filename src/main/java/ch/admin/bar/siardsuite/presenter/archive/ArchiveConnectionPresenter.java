package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.SiardTooltip;
import ch.admin.bar.siardsuite.component.StepperButtonBox;
import ch.admin.bar.siardsuite.database.DatabaseConnectionProperties;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.util.UserPreferences;
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

import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static ch.admin.bar.siardsuite.component.StepperButtonBox.Type.DEFAULT;
import static ch.admin.bar.siardsuite.database.DatabaseConnectionProperties.*;
import static ch.admin.bar.siardsuite.util.SiardEvent.UPDATE_STEPPER_DBLOAD_EVENT;
import static ch.admin.bar.siardsuite.util.UserPreferences.KeyIndex.*;
import static ch.admin.bar.siardsuite.util.UserPreferences.NodePath.DATABASE_CONNECTION;

public class ArchiveConnectionPresenter extends StepperPresenter {

    private static final String DBSERVER_ORGANISATION_ORG = "dbserver.organisation.org";
    private static final String TEST_DB = "test-db";

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
        this.init(controller, model, stage);

        addTextWithStyles();
        addFormText();

        if (controller.recentDatabaseConnection != null) {
            addRecentDatabaseConnection();
        }
        tooltip = new SiardTooltip("archiveConnection.view.tooltip");
        errorMessage.setVisible(false);
        buttonsBox = new StepperButtonBox().make(DEFAULT);
        borderPane.setBottom(buttonsBox);
        setListeners(stepper);
    }

    private void addTextWithStyles() {
        I18n.bind(title.textProperty(), "connection.view.title");
        I18n.bind(subtitleLeft.textProperty(), "connection.view.subtitleLeft");
        I18n.bind(subtitleRight.textProperty(), "connection.view.subtitleRight");
        I18n.bind(textLeft.textProperty(), "connection.view.textLeft");
        I18n.bind(textRight.textProperty(), "connection.view.textRight");

        for (int i = 0; i < textFlow.getChildren().size(); i++) {
            Text text = (Text) textFlow.getChildren().get(i);
            I18n.bind(text.textProperty(), "connection.view.text" + i);
        }
        text1.getStyleClass().add("bold");
        text3.getStyleClass().add("bold");
    }

    private void addFormText() {
        I18n.bind(dbServerField.floatingTextProperty(), "connection.view.dbServer.label");
        I18n.bind(dbServerField.promptTextProperty(), "connection.view.dbServer.prompt");
        I18n.bind(portField.floatingTextProperty(), "connection.view.port.label");
        I18n.bind(dbNameField.floatingTextProperty(), "connection.view.databaseName.label");
        I18n.bind(usernameField.floatingTextProperty(), "connection.view.username.label");
        I18n.bind(passwordField.floatingTextProperty(), "connection.view.password.label");
        I18n.bind(urlField.floatingTextProperty(), "connection.view.url.label");
        I18n.bind(toggleSave.textProperty(), "archiveConnection.view.toggleSave");
        I18n.bind(connectionName.floatingTextProperty(), "archiveConnection.view.connectionName.label");
    }

    private void addRecentDatabaseConnection() {
        Preferences preferences = UserPreferences.node(DATABASE_CONNECTION).node(controller.recentDatabaseConnection);
        dbTypeString = preferences.get(DATABASE_SYSTEM.name(), "");
        controller.setDatabaseType(dbTypeString);
        dbServerField.setText(preferences.get(DATABASE_SERVER.name(), ""));
        portField.setText(preferences.get(PORT_NUMBER.name(), ""));
        dbNameField.setText(preferences.get(DATABASE_NAME.name(), ""));
        usernameField.setText(preferences.get(USER_NAME.name(), ""));
        urlField.setText(preferences.get(CONNECTION_URL.name(), ""));
        connectionName.setText(controller.recentDatabaseConnection);
        controller.recentDatabaseConnection = null;
    }

    private void setListeners(MFXStepper stepper) {
        stepper.addEventHandler(SiardEvent.UPDATE_STEPPER_DBTYPE_EVENT, event -> {
            // TODO MSAccess-DB needs different Fields for selecting File- #CR457
            DatabaseConnectionProperties.DatabaseProperties props = model.getDatabaseProps();
            this.dbTypeString = props.defaultUrl().replace(PRODUCT, props.product());
            this.portString = props.port();
            String url = this.dbTypeString
                    .replace(HOST, DBSERVER_ORGANISATION_ORG)
                    .replace(PORT, portString)
                    .replace(DB_NAME, TEST_DB);

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

        buttonsBox.next().setOnAction((event) -> {
            if (dbServerField.getText().isEmpty()) {
                I18n.bind(errorMessage.textProperty(), "connection.view.error.database.server");
                errorMessage.setVisible(true);
            } else if (dbNameField.getText().isEmpty()) {
                I18n.bind(errorMessage.textProperty(), "connection.view.error.database.name");
                errorMessage.setVisible(true);
            } else if (usernameField.getText().isEmpty()) {
                I18n.bind(errorMessage.textProperty(), "connection.view.error.user.name");
                errorMessage.setVisible(true);
            } else if (passwordField.getText().isEmpty()) {
                I18n.bind(errorMessage.textProperty(), "connection.view.error.user.password");
                errorMessage.setVisible(true);
            } else if (urlField.getText().isEmpty()) {
                I18n.bind(errorMessage.textProperty(), "connection.view.error.connection.url");
                errorMessage.setVisible(true);
            } else if (toggleSave.isSelected() && connectionName.getText().isEmpty()) {
                I18n.bind(errorMessage.textProperty(), "connection.view.error.connection.name");
                errorMessage.setVisible(true);
            } else if (toggleSave.isSelected() && connectionName.getText().contains("/")) {
                I18n.bind(errorMessage.textProperty(), "connection.view.error.connection.name.symbol");
                errorMessage.setVisible(true);
            } else {
                if (toggleSave.isSelected()) {
                    try {
                        final Preferences preferences = UserPreferences.push(DATABASE_CONNECTION, TIMESTAMP, Comparator.reverseOrder(), connectionName.getText());
                        preferences.put(DATABASE_SYSTEM.name(), model.getDatabaseProduct().get());
                        preferences.put(DATABASE_SERVER.name(), dbServerField.getText());
                        preferences.put(PORT_NUMBER.name(), portField.getText());
                        preferences.put(DATABASE_NAME.name(), dbNameField.getText());
                        preferences.put(USER_NAME.name(), usernameField.getText());
                        preferences.put(USER_PASSWORD.name(), passwordField.getText());
                        preferences.put(CONNECTION_URL.name(), urlField.getText());
                        preferences.put(STORAGE_DATE.name(), I18n.getLocaleDate(LocalDate.now().toString()));
                        preferences.put(TIMESTAMP.name(), String.valueOf(Clock.systemDefaultZone().millis()));
                    } catch (BackingStoreException e) {
                        throw new RuntimeException(e);
                    }
                }
                controller.updateConnectionData(urlField.getText(), this.usernameField.getText(), this.dbNameField.getText(), this.passwordField.getText());
                errorMessage.setVisible(false);
                stepper.next();
                stepper.fireEvent(new SiardEvent(UPDATE_STEPPER_DBLOAD_EVENT));
            }

        });
        this.buttonsBox.previous().setOnAction((event) -> {
            stepper.previous();
        });
        this.buttonsBox.cancel().setOnAction((event) -> stage.openDialog(View.ARCHIVE_ABORT_DIALOG));
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
