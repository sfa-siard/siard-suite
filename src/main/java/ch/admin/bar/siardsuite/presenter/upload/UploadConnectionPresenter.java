package ch.admin.bar.siardsuite.presenter.upload;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.ButtonBox;
import ch.admin.bar.siardsuite.component.SiardToolip;
import ch.admin.bar.siardsuite.component.SiardTooltip;
import ch.admin.bar.siardsuite.database.DatabaseProperties;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.model.database.DatabaseSchema;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.presenter.ValidationProperties;
import ch.admin.bar.siardsuite.presenter.ValidationProperty;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.util.preferences.UserPreferences;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.prefs.Preferences;

import static ch.admin.bar.siardsuite.component.ButtonBox.Type.DEFAULT;
import static ch.admin.bar.siardsuite.util.preferences.UserPreferences.KeyIndex.CONNECTION_URL;
import static ch.admin.bar.siardsuite.util.preferences.UserPreferences.KeyIndex.DATABASE_NAME;
import static ch.admin.bar.siardsuite.util.preferences.UserPreferences.KeyIndex.DATABASE_SERVER;
import static ch.admin.bar.siardsuite.util.preferences.UserPreferences.KeyIndex.DATABASE_SYSTEM;
import static ch.admin.bar.siardsuite.util.preferences.UserPreferences.KeyIndex.PORT_NUMBER;
import static ch.admin.bar.siardsuite.util.preferences.UserPreferences.KeyIndex.USER_NAME;
import static ch.admin.bar.siardsuite.util.preferences.UserPreferences.NodePath.DATABASE_CONNECTION;

public class UploadConnectionPresenter extends StepperPresenter {

    private List<DatabaseSchema> schemas = new ArrayList<>();
    private final Map<String, String> schemaMap = new HashMap<>();
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
    public TextField dbServerField;
    @FXML
    public TextField dbNameField;
    @FXML
    public TextField usernameField;
    @FXML
    public TextField urlField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField portField;
    @FXML
    public BorderPane borderPane;
    @FXML
    private ButtonBox buttonsBox;
    @FXML
    public MFXButton infoButton;
    @FXML
    private Tooltip tooltip;
    @FXML
    public Label titleNewSchemaName;
    @FXML
    public VBox schemaFields;
    @FXML
    public Label currentNameLabel;
    @FXML
    public Label newNameLabel;
    @FXML
    public Label dbServerLabel;
    @FXML
    public Label portLabel;
    @FXML
    public Label dbNameLabel;
    @FXML
    public Label usernameLabel;
    @FXML
    public Label passwordLabel;
    @FXML
    public Label urlLabel;

    @FXML
    public Label dbServerValidationMsg;
    @FXML
    public Label dbNameValidationMsg;
    @FXML
    public Label portValidationMsg;
    @FXML
    public Label userNameValidationMsg;
    @FXML
    public Label passwordValidationMsg;
    @FXML
    public Label urlValidationMsg;
    @FXML
    public Label schemaValidationMsg;


    @Override
    public void init(Controller controller, RootStage stage) {
        this.controller = controller;
        this.stage = stage;
    }

    @Override
    public void init(Controller controller, RootStage stage, MFXStepper stepper) {
        this.init(controller, stage);
        this.schemas = controller.getSiardArchive().schemas();
        addTextWithStyles();
        addFormText();

        if (controller.recentDatabaseConnection != null) {
            addRecentDatabaseConnection();
            initSchemaFields();
        }

        tooltip = new SiardTooltip("uploadConnection.view.tooltip");
        buttonsBox = new ButtonBox().make(DEFAULT);
        borderPane.setBottom(buttonsBox);
        setListeners(stepper);
    }

    private void addTextWithStyles() {
        // TODO: use generic names - its really the same.. for upload and archiving
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
        I18n.bind(dbServerLabel.textProperty(), "connection.view.dbServer.label");
        I18n.bind(dbServerField.promptTextProperty(), "connection.view.dbServer.prompt");
        I18n.bind(portLabel.textProperty(), "connection.view.port.label");
        I18n.bind(dbNameLabel.textProperty(), "connection.view.databaseName.label");
        I18n.bind(usernameLabel.textProperty(), "connection.view.username.label");
        I18n.bind(passwordLabel.textProperty(), "connection.view.password.label");
        I18n.bind(urlLabel.textProperty(), "connection.view.url.label");
        I18n.bind(titleNewSchemaName.textProperty(), "uploadConnection.view.new.schema.name");
        I18n.bind(currentNameLabel.textProperty(), "uploadConnection.view.current.name");
        I18n.bind(newNameLabel.textProperty(), "uploadConnection.view.new.name");
    }

    private void addRecentDatabaseConnection() {
        Preferences preferences = UserPreferences.node(DATABASE_CONNECTION).node(controller.recentDatabaseConnection);
        String dbTypeString = preferences.get(DATABASE_SYSTEM.name(), "");
        controller.setDatabaseType(dbTypeString);
        dbServerField.setText(preferences.get(DATABASE_SERVER.name(), ""));
        portField.setText(preferences.get(PORT_NUMBER.name(), ""));
        dbNameField.setText(preferences.get(DATABASE_NAME.name(), ""));
        usernameField.setText(preferences.get(USER_NAME.name(), ""));
        urlField.setText(preferences.get(CONNECTION_URL.name(), ""));
        controller.recentDatabaseConnection = null;
    }

    private void setListeners(MFXStepper stepper) {
        stepper.addEventHandler(SiardEvent.UPLOAD_DBMS_SELECTED, event -> {
            // TODO MSAccess-DB needs different Fields for selecting File- #CR457
            DatabaseProperties props = controller.getDatabaseProps();
            portField.setText(props.port());
            portField.setPromptText(props.port());
            urlField.setPromptText(props.jdbcUrl());
            initSchemaFields();
        });

        dbServerField.setOnKeyReleased(this::handleKeyEvent);
        dbNameField.setOnKeyReleased(this::handleKeyEvent);
        portField.setOnKeyReleased(this::handleKeyEvent);
        usernameField.setOnKeyReleased(this::handleKeyEvent);

        new SiardToolip(infoButton, tooltip).setup();

        buttonsBox.next().setOnAction((event) -> {

            boolean allPropsValid = this.validateProperties();
            boolean validSchemaFields = validSchemaFields();
            if (allPropsValid && validSchemaFields) {
                controller.updateConnectionData(urlField.getText(),
                        this.usernameField.getText(),
                        this.dbNameField.getText(),
                        this.passwordField.getText());
                controller.updateSchemaMap(schemaMap);
                stepper.next();
                stepper.fireEvent(new SiardEvent(SiardEvent.UPLOAD_CONNECTION_UPDATED));
                passwordField.setText("");
            }
        });

        this.buttonsBox.previous().setOnAction((event) -> stepper.previous());
        this.buttonsBox.cancel().setOnAction((event) -> stage.openDialog(View.UPLOAD_ABORT_DIALOG));
    }

    private boolean validSchemaFields() {
        if (hasValidSchemaFields()) return true;
        I18n.bind(schemaValidationMsg.textProperty(), "uploadConnection.view.error.schema.name");
        schemaValidationMsg.setVisible(true);
        return false;
    }

    private boolean validateProperties() {
        ValidationProperties validationProperties = new ValidationProperties(Arrays.asList(new ValidationProperty(
                        dbServerField,
                        dbServerValidationMsg,
                        "connection.view.error.database.server"),
                new ValidationProperty(
                        dbNameField,
                        dbNameValidationMsg,
                        "connection.view.error.database.name"),
                new ValidationProperty(
                        usernameField,
                        userNameValidationMsg,
                        "connection.view.error.user.name"),

                new ValidationProperty(
                        passwordField,
                        passwordValidationMsg,
                        "connection.view.error.user.password"),
                new ValidationProperty(
                        urlField,
                        urlValidationMsg,
                        "connection.view.error.connection.url")));

        return validationProperties.validate();
    }

    private boolean hasValidSchemaFields() {
        AtomicBoolean result = new AtomicBoolean(true);
        schemaFields.getChildren().forEach(container -> {
            List<Node> nodes = ((HBox) container).getChildren();
            if (nodes.size() > 2) {
                String currentName = ((Label) nodes.get(0)).getText();
                String newName = ((TextField) nodes.get(2)).getText();
                if (!currentName.isEmpty() && !newName.isEmpty()) {
                    schemaMap.put(currentName, newName);
                } else {
                    result.set(false);
                }
            }
        });

        return result.get();
    }

    private void initSchemaFields() {
        for (DatabaseSchema schema : schemas) {
            Label currentName = new Label();
            Label iconLabel = new Label();
            TextField newName = new TextField();
            currentName.setText(schema.getName());
            newName.setText(schema.getName());
            HBox container = new HBox();
            container.setPrefSize(200.0, 48.0);
            currentName.setPrefSize(253.0, 48.0);
            iconLabel.setPrefSize(48.0, 48.0);
            iconLabel.getStyleClass().add("arrow-right-icon");
            newName.setPrefSize(277.0, 48.0);
            newName.getStyleClass().add("form-field");
            container.getChildren().addAll(currentName, iconLabel, newName);
            HBox.setMargin(currentName, new Insets(10, 0, 0, 0));
            HBox.setMargin(iconLabel, new Insets(10, 0, 0, 0));
            HBox.setMargin(newName, new Insets(10, 0, 0, 0));
            schemaFields.getChildren().add(container);
        }
    }

    private void handleKeyEvent(KeyEvent event) {
        String inputText = event.getText();
        if (inputText != null) {
            urlField.setText(controller.getDatabaseProps()
                    .jdbcUrl(dbServerField.getText(), portField.getText(), dbNameField.getText()));
        }
        event.consume();
    }
}
