package ch.admin.bar.siardsuite.presenter.upload;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.SiardTooltip;
import ch.admin.bar.siardsuite.component.StepperButtonBox;
import ch.admin.bar.siardsuite.database.DatabaseConnectionProperties;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.model.database.SiardArchiveDatabaseNameVisitor;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.util.UserPreferences;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXStepper;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.prefs.Preferences;

import static ch.admin.bar.siardsuite.component.StepperButtonBox.Type.DEFAULT;
import static ch.admin.bar.siardsuite.database.DatabaseConnectionProperties.*;
import static ch.admin.bar.siardsuite.util.UserPreferences.KeyIndex.*;
import static ch.admin.bar.siardsuite.util.UserPreferences.NodePath.DATABASE_CONNECTION;

public class UploadConnectionPresenter extends StepperPresenter implements SiardArchiveDatabaseNameVisitor {

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
    public BorderPane borderPane;
    @FXML
    private StepperButtonBox buttonsBox;
    @FXML
    public MFXButton infoButton;
    @FXML
    private Tooltip tooltip;
    @FXML
    public Label titleNewSchemaName;

    @FXML
    public MFXTextField currentName;
    @FXML
    public MFXTextField newName;

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        this.stage.setHeight(1200);
        this.tooltip = new SiardTooltip("uploadConnection.view.tooltip");
    }

    @Override
    public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) {
        init(controller, model, stage);

        addTextWithStyles();
        addFormText();

        if (controller.recentDatabaseConnection != null) {
            addRecentDatabaseConnection();
        }

        errorMessage.setVisible(false);
        buttonsBox = new StepperButtonBox().make(DEFAULT);
        borderPane.setBottom(buttonsBox);
        this.model.getArchive().databaseName(this);
        setListeners(stepper);
    }

    private void addTextWithStyles() {
        // TODO: use generic names - its really the same.. for upload and archiving
        I18n.bind(title.textProperty(),"archiveConnection.view.title");
        I18n.bind(subtitleLeft.textProperty(),"archiveConnection.view.subtitleLeft");
        I18n.bind(subtitleRight.textProperty(),"archiveConnection.view.subtitleRight");
        I18n.bind(textLeft.textProperty(),"archiveConnection.view.textLeft");
        I18n.bind(textRight.textProperty(),"archiveConnection.view.textRight");

        for (int i = 0; i < textFlow.getChildren().size(); i++) {
            Text text = (Text) textFlow.getChildren().get(i);
            I18n.bind(text.textProperty(), "archiveConnection.view.text" + i);
        }
        text1.getStyleClass().add("bold");
        text3.getStyleClass().add("bold");
    }

    private void addFormText() {
        I18n.bind(dbServerField.floatingTextProperty(), "archiveConnection.view.dbServer.label");
        I18n.bind(dbServerField.promptTextProperty(), "archiveConnection.view.dbServer.prompt");
        I18n.bind(portField.floatingTextProperty(), "archiveConnection.view.port.label");
        I18n.bind(dbNameField.floatingTextProperty(), "archiveConnection.view.databaseName.label");
        I18n.bind(usernameField.floatingTextProperty(), "archiveConnection.view.username.label");
        I18n.bind(passwordField.floatingTextProperty(), "archiveConnection.view.password.label");
        I18n.bind(urlField.floatingTextProperty(), "archiveConnection.view.url.label");
        I18n.bind(titleNewSchemaName.textProperty(), "uploadConnection.view.new.schema.name");
        I18n.bind(currentName.floatingTextProperty(), "uploadConnection.view.current.name");
        I18n.bind(newName.floatingTextProperty(), "uploadConnection.view.new.name");
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
        controller.recentDatabaseConnection = null;
    }

    private void setListeners(MFXStepper stepper) {
        stepper.addEventHandler(SiardEvent.UPLOAD_DBMS_SELECTED, event -> {
            // TODO MSAccess-DB needs different Fields for selecting File- #CR457
            DatabaseConnectionProperties.DatabaseProperties props = model.getDatabaseProps();
            dbTypeString = props.defaultUrl().replace(PRODUCT, props.product());
            portString = props.port();
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


        infoButton.setOnMouseMoved(event -> {
            Bounds boundsInScreen = infoButton.localToScreen(infoButton.getBoundsInLocal());
            tooltip.show(infoButton,
                         (boundsInScreen.getMaxX() - boundsInScreen.getWidth() / 2) - tooltip.getWidth() / 2,
                         boundsInScreen.getMaxY() - boundsInScreen.getHeight() - tooltip.getHeight());
        });

        infoButton.setOnMouseExited(event -> tooltip.hide());

        buttonsBox.next().setOnAction((event) -> {
            if (dbServerField.getText().isEmpty()) {
                I18n.bind(errorMessage.textProperty(), "archiveConnection.view.error.database.server");
                errorMessage.setVisible(true);
            } else if (dbNameField.getText().isEmpty()) {
                I18n.bind(errorMessage.textProperty(), "archiveConnection.view.error.database.name");
                errorMessage.setVisible(true);
            } else if (usernameField.getText().isEmpty()) {
                I18n.bind(errorMessage.textProperty(), "archiveConnection.view.error.user.name");
                errorMessage.setVisible(true);
            } else if (passwordField.getText().isEmpty()) {
                I18n.bind(errorMessage.textProperty(),"archiveConnection.view.error.user.password");
                errorMessage.setVisible(true);
            } else if (urlField.getText().isEmpty()) {
                I18n.bind(errorMessage.textProperty(), "archiveConnection.view.error.connection.url");
                errorMessage.setVisible(true);
            } else {
                controller.updateConnectionData(urlField.getText(), this.usernameField.getText(), this.dbNameField.getText(), this.passwordField.getText());
                errorMessage.setVisible(false);
                stage.setHeight(950);
                stepper.next();
                stepper.fireEvent(getUpdateEvent(SiardEvent.UPDATE_STEPPER_DBLOAD_EVENT));
            }

        });

        this.buttonsBox.previous().setOnAction((event) -> {
            stepper.previous();
            this.stage.setHeight(700.00); // TODO: should probably move to the previes step itself...
        });
        this.buttonsBox.cancel().setOnAction((event) -> stage.openDialog(View.UPLOAD_ABORT_DIALOG));
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

    @Override
    public void visit(String databaseName) {
        this.currentName.setText(databaseName);
        this.newName.setText(databaseName);
    }
}