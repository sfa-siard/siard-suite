package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.ButtonBox;
import ch.admin.bar.siardsuite.component.SiardToolip;
import ch.admin.bar.siardsuite.component.SiardTooltip;
import ch.admin.bar.siardsuite.database.model.Dbms;
import ch.admin.bar.siardsuite.database.model.DbmsConnectionProperties;
import ch.admin.bar.siardsuite.database.model.FileBasedDbms;
import ch.admin.bar.siardsuite.database.model.FileBasedDbmsConnectionProperties;
import ch.admin.bar.siardsuite.database.model.ServerBasedDbms;
import ch.admin.bar.siardsuite.database.model.ServerBasedDbmsConnectionProperties;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.presenter.connection.ConnectionPropertiesForm;
import ch.admin.bar.siardsuite.presenter.connection.FileBasedDbmsConnectionPropertiesForm;
import ch.admin.bar.siardsuite.presenter.connection.ServerBasedDbmsConnectionPropertiesForm;
import ch.admin.bar.siardsuite.util.CastHelper;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.util.preferences.DbConnection;
import ch.admin.bar.siardsuite.util.preferences.UserPreferences;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lombok.val;

import java.util.Optional;

import static ch.admin.bar.siardsuite.component.ButtonBox.Type.DEFAULT;

public class ArchiveConnectionPresenter extends StepperPresenter {

    private static final I18nKey TITLE = I18nKey.of("connection.view.title");
    private static final I18nKey SUBTITLE_LEFT = I18nKey.of("connection.view.subtitleLeft");
    private static final I18nKey SUBTITLE_RIGHT = I18nKey.of("connection.view.subtitleRight");
    private static final I18nKey TEXT_LEFT = I18nKey.of("connection.view.textLeft");
    private static final I18nKey TEXT_RIGHT = I18nKey.of("connection.view.textRight");

    @FXML
    public Text title;
    @FXML
    public Text text1;
    @FXML
    public Text text3;
    @FXML
    public TextFlow textFlow;

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

    @FXML
    public Label connectionLabel;

    @FXML
    public Label connectionValidationMsg;

    private ConnectionPropertiesForm connectionPropertiesForm;

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

        tooltip = new SiardTooltip("archiveConnection.view.tooltip");
        buttonsBox = new ButtonBox().make(DEFAULT);
        borderPane.setBottom(buttonsBox);
        setListeners(stepper);
    }

    private void addTextWithStyles() {
        title.textProperty().bind(DisplayableText.of(TITLE).bindable());

        for (int i = 0; i < textFlow.getChildren().size(); i++) {
            Text text = (Text) textFlow.getChildren().get(i);
            I18n.bind(text.textProperty(), "connection.view.text" + i);
        }
        text1.getStyleClass().add("bold");
        text3.getStyleClass().add("bold");
    }

    private void addFormText() {
        I18n.bind(toggleSave.textProperty(), "archiveConnection.view.toggleSave");
        I18n.bind(connectionLabel.textProperty(), "archiveConnection.view.connectionName.label");
    }

    private <T extends DbmsConnectionProperties> void handlePropertiesForm(
            final Dbms<T> dbms,
            final Optional<DbmsConnectionProperties<T>> initialValue
    ) {
        connectionPropertiesForm = this.getConnectionPropertiesForm(dbms, initialValue);
        HBox.setHgrow(connectionPropertiesForm, Priority.ALWAYS);

        formContainer.getChildren().clear();
        formContainer.getChildren().add(connectionPropertiesForm);
    }

    private <T extends DbmsConnectionProperties> ConnectionPropertiesForm getConnectionPropertiesForm(
            final Dbms<T> dbms,
            final Optional<DbmsConnectionProperties<T>> initialValue
    ) {
        if (dbms instanceof ServerBasedDbms) {
            return new ServerBasedDbmsConnectionPropertiesForm(
                    (ServerBasedDbms) dbms,
                    initialValue.flatMap(CastHelper.tryCast(ServerBasedDbmsConnectionProperties.class)));
        }

        if (dbms instanceof FileBasedDbms) {
            return new FileBasedDbmsConnectionPropertiesForm(
                    (FileBasedDbms) dbms,
                    initialValue.flatMap(CastHelper.tryCast(FileBasedDbmsConnectionProperties.class)));
        }

        throw new IllegalArgumentException("Unsupported DBMS: " + dbms);
    }

    private void setListeners(MFXStepper stepper) {
        stepper.addEventHandler(SiardEvent.RECENT_CONNECTION_SELECTED_EVENT, event -> {
            val recentConnection = event.getRecentConnectionData();

            final Dbms dbms = recentConnection.mapToDbmsConnectionData().getDbms();
            final DbmsConnectionProperties properties = recentConnection.mapToDbmsConnectionData().getProperties();

            handlePropertiesForm(dbms, Optional.of(properties));

            connectionName.setText(recentConnection.getName());
        });

        stepper.addEventHandler(SiardEvent.UPDATE_STEPPER_DBTYPE_EVENT, event -> {
            handlePropertiesForm(event.getSelectedDbms(), Optional.empty());
            connectionName.setText("");
        });

        toggleSave.setOnAction(event -> {
            this.connectionLabel.setVisible(!this.connectionLabel.isVisible());
            this.connectionName.setVisible(!this.connectionName.isVisible());
        });

        new SiardToolip(infoButton, tooltip).setup();

        buttonsBox.next().setOnAction((event) ->
                connectionPropertiesForm.tryGetValidConnectionData()
                        .ifPresent(connectionData -> {
                            if (toggleSave.isSelected()) {
                                UserPreferences.push(DbConnection.from(
                                        connectionData,
                                        connectionName.getText()
                                ));
                            }

                            stepper.next();
                            stepper.fireEvent(new SiardEvent.DbmsConnectionDataReadyEvent(connectionData));
                        }));

        this.buttonsBox.previous().setOnAction((event) -> stepper.previous());
        this.buttonsBox.cancel().setOnAction((event) -> stage.openDialog(View.ARCHIVE_ABORT_DIALOG));
    }
}
