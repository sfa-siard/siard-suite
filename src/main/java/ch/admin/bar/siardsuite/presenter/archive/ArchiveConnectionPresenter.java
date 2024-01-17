package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.ButtonBox;
import ch.admin.bar.siardsuite.component.SiardToolip;
import ch.admin.bar.siardsuite.component.SiardTooltip;
import ch.admin.bar.siardsuite.database.DbmsRegistry;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.presenter.connection.ServerBasedDbmsConnectionPropertiesForm;
import ch.admin.bar.siardsuite.presenter.connection.SelectFileConnectionPropertiesForm;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.util.UserPreferences;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lombok.val;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static ch.admin.bar.siardsuite.component.ButtonBox.Type.DEFAULT;
import static ch.admin.bar.siardsuite.util.SiardEvent.UPDATE_STEPPER_DBLOAD_EVENT;
import static ch.admin.bar.siardsuite.util.UserPreferences.KeyIndex.CONNECTION_URL;
import static ch.admin.bar.siardsuite.util.UserPreferences.KeyIndex.DATABASE_SYSTEM;
import static ch.admin.bar.siardsuite.util.UserPreferences.KeyIndex.STORAGE_DATE;
import static ch.admin.bar.siardsuite.util.UserPreferences.KeyIndex.TIMESTAMP;
import static ch.admin.bar.siardsuite.util.UserPreferences.NodePath.DATABASE_CONNECTION;

public class ArchiveConnectionPresenter extends StepperPresenter {

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
    public TextField urlField;
    @FXML
    public TextField connectionName;
    @FXML
    public MFXToggleButton toggleSave;
    @FXML
    public BorderPane borderPane;

    @FXML
    public HBox formContainer;

    @FXML
    private ButtonBox buttonsBox;
    @FXML
    public MFXButton infoButton;
    @FXML
    private Tooltip tooltip;

    public Label urlLabel;
    @FXML
    public Label connectionLabel;

    @FXML
    public Label urlValidationMsg;
    @FXML
    public Label connectionValidationMsg;

    @Override
    public void init(Controller controller, RootStage stage) {
        this.controller = controller;
        this.stage = stage;
    }

    @Override
    public void init(Controller controller, RootStage stage, MFXStepper stepper) {
        this.init(controller, stage);

        addTextWithStyles();
        addFormText();

        if (controller.recentDatabaseConnection != null) {
            addRecentDatabaseConnection();
        }
        tooltip = new SiardTooltip("archiveConnection.view.tooltip");
        buttonsBox = new ButtonBox().make(DEFAULT);
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
        I18n.bind(urlLabel.textProperty(), "connection.view.url.label");
        I18n.bind(toggleSave.textProperty(), "archiveConnection.view.toggleSave");
        I18n.bind(connectionLabel.textProperty(), "archiveConnection.view.connectionName.label");
    }

    private void addRecentDatabaseConnection() {
        Preferences preferences = UserPreferences.node(DATABASE_CONNECTION).node(controller.recentDatabaseConnection);
        controller.setDatabaseType(preferences.get(DATABASE_SYSTEM.name(), ""));
        urlField.setText(preferences.get(CONNECTION_URL.name(), ""));
        connectionName.setText(controller.recentDatabaseConnection);
        controller.recentDatabaseConnection = null;
    }

    private Pane getConnectionPropertiesForm(final DbmsRegistry.Dbms<?> dbms) {
        if (dbms.getExampleConfiguration() instanceof DbmsRegistry.ServerBasedDbms) {
            return new ServerBasedDbmsConnectionPropertiesForm((DbmsRegistry.ServerBasedDbms) dbms.getExampleConfiguration());
        }

        if (dbms.getExampleConfiguration() instanceof DbmsRegistry.FileBasedDbms) {
            return new SelectFileConnectionPropertiesForm((DbmsRegistry.FileBasedDbms) dbms.getExampleConfiguration());
        }

        throw new IllegalArgumentException("Unsupported DBMS: " + dbms);
    }

    private void setListeners(MFXStepper stepper) {
        stepper.addEventHandler(SiardEvent.UPDATE_STEPPER_DBTYPE_EVENT, event -> {
            val form = getConnectionPropertiesForm(event.getSelectedDbms());
            HBox.setHgrow(form, Priority.ALWAYS);
            formContainer.getChildren().clear();
            formContainer.getChildren().add(form);


            // TODO MSAccess-DB needs different Fields for selecting File- #CR457
//            DatabaseProperties databaseProps = controller.getDatabaseProps();
//            String url = databaseProps.jdbcUrl();
//            portField.setText(databaseProps.port());
//            portField.setPromptText(databaseProps.port());
//            urlField.setPromptText(url);
        });

//        dbServerField.setOnKeyReleased(this::handleKeyEvent);
//        dbNameField.setOnKeyReleased(this::handleKeyEvent);
//        portField.setOnKeyReleased(this::handleKeyEvent);
//        usernameField.setOnKeyReleased(this::handleKeyEvent);

        toggleSave.setOnAction(event -> {
            this.connectionLabel.setVisible(!this.connectionLabel.isVisible());
            this.connectionName.setVisible(!this.connectionName.isVisible());
        });

        new SiardToolip(infoButton, tooltip).setup();

        buttonsBox.next().setOnAction((event) -> {
            if (this.validateProperties()) {
                if (toggleSave.isSelected()) {
                    try {
                        final Preferences preferences = UserPreferences.push(DATABASE_CONNECTION,
                                TIMESTAMP,
                                Comparator.reverseOrder(),
                                connectionName.getText());
                        preferences.put(DATABASE_SYSTEM.name(), controller.getDatabaseProduct().get());
//                        preferences.put(DATABASE_SERVER.name(), dbServerField.getText());
//                        preferences.put(PORT_NUMBER.name(), portField.getText());
//                        preferences.put(DATABASE_NAME.name(), dbNameField.getText());
//                        preferences.put(USER_NAME.name(), usernameField.getText());
//                        preferences.put(USER_PASSWORD.name(), passwordField.getText());
                        preferences.put(CONNECTION_URL.name(), urlField.getText());
                        preferences.put(STORAGE_DATE.name(), I18n.getLocaleDate(LocalDate.now().toString()));
                        preferences.put(TIMESTAMP.name(), String.valueOf(Clock.systemDefaultZone().millis()));
                    } catch (BackingStoreException e) {
                        throw new RuntimeException(e);
                    }
                }
//                controller.updateConnectionData(urlField.getText(),
//                                                this.usernameField.getText(),
//                                                this.dbNameField.getText(),
//                                                this.passwordField.getText());
                stepper.next();
                stepper.fireEvent(new SiardEvent(UPDATE_STEPPER_DBLOAD_EVENT));
//                passwordField.setText("");
            }

        });
        this.buttonsBox.previous().setOnAction((event) -> stepper.previous());
        this.buttonsBox.cancel().setOnAction((event) -> stage.openDialog(View.ARCHIVE_ABORT_DIALOG));
    }


    private void handleKeyEvent(KeyEvent event) {
        String inputText = event.getText();
        if (inputText != null) {
//            urlField.setText(controller.getDatabaseProps().jdbcUrl(dbServerField.getText(), portField.getText(),
//                                                                   dbNameField.getText()));
        }
        event.consume();
    }

    private boolean validateProperties() {
        return false;
    }

}
