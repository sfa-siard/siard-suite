package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.ButtonBox;
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
import ch.admin.bar.siardsuite.presenter.connection.fields.StringFormField;
import ch.admin.bar.siardsuite.util.CastHelper;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.util.Validator;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.util.preferences.DbConnection;
import ch.admin.bar.siardsuite.util.preferences.UserPreferences;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lombok.val;

import java.util.Optional;

import static ch.admin.bar.siardsuite.component.ButtonBox.Type.DEFAULT;

public class ArchiveConnectionPresenter extends StepperPresenter {

    private static final I18nKey TITLE = I18nKey.of("connection.view.title");

    private static final I18nKey TEXT_0 = I18nKey.of("connection.view.text0");
    private static final I18nKey TEXT_1 = I18nKey.of("connection.view.text1");
    private static final I18nKey TEXT_2 = I18nKey.of("connection.view.text2");
    private static final I18nKey TEXT_3 = I18nKey.of("connection.view.text3");
    private static final I18nKey TEXT_4 = I18nKey.of("connection.view.text4");

    private static final I18nKey TOGGLE_SAVE = I18nKey.of("archiveConnection.view.toggleSave");
    private static final I18nKey TOGGLE_SAVE_INFO = I18nKey.of("archiveConnection.view.tooltip");
    private static final I18nKey CONNECTION_NAME = I18nKey.of("archiveConnection.view.connectionName.label");

    @FXML
    public Text title;
    @FXML
    public Text text0;
    @FXML
    public Text text1;
    @FXML
    public Text text2;
    @FXML
    public Text text3;
    @FXML
    public Text text4;
    @FXML
    public TextFlow textFlow;

    @FXML
    public BorderPane borderPane;

    @FXML
    public VBox formContainer;

    @FXML
    private ButtonBox buttonsBox;

    private ConnectionPropertiesForm connectionPropertiesForm;

    private StringFormField connectionNameField;

    @Override
    public void init(Controller controller, RootStage stage) {
        this.controller = controller;
        this.stage = stage;
    }

    @Override
    public void init(Controller controller, RootStage stage, MFXStepper stepper) {
        this.init(controller, stage);

        title.textProperty().bind(DisplayableText.of(TITLE).bindable());

        text0.textProperty().bind(DisplayableText.of(TEXT_0).bindable());
        text1.textProperty().bind(DisplayableText.of(TEXT_1).bindable());
        text2.textProperty().bind(DisplayableText.of(TEXT_2).bindable());
        text3.textProperty().bind(DisplayableText.of(TEXT_3).bindable());
        text4.textProperty().bind(DisplayableText.of(TEXT_4).bindable());

        text1.getStyleClass().add("bold");
        text3.getStyleClass().add("bold");

        buttonsBox = new ButtonBox().make(DEFAULT);
        borderPane.setBottom(buttonsBox);

        setListeners(stepper);
    }

    private <T extends DbmsConnectionProperties> void handlePropertiesForm(
            final Dbms<T> dbms,
            final Optional<DbmsConnectionProperties<T>> initialValue
    ) {
        connectionPropertiesForm = this.getConnectionPropertiesForm(dbms, initialValue);
        HBox.setHgrow(connectionPropertiesForm, Priority.ALWAYS);

        connectionNameField = StringFormField.builder()
                .title(DisplayableText.of(CONNECTION_NAME))
                .validator(Validator.IS_NOT_EMPTY_STRING_VALIDATOR)
                .hint(DisplayableText.of(TOGGLE_SAVE_INFO))
                .deactivable(true)
                .build();
        VBox.setMargin(connectionNameField, new Insets(25));
        VBox.setVgrow(connectionNameField, Priority.ALWAYS);

        formContainer.getChildren().clear();
        formContainer.getChildren().addAll(connectionPropertiesForm, connectionNameField);
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

            connectionNameField.setValue(recentConnection.getName());
        });

        stepper.addEventHandler(SiardEvent.UPDATE_STEPPER_DBTYPE_EVENT, event -> {
            handlePropertiesForm(event.getSelectedDbms(), Optional.empty());
        });

        buttonsBox.next().setOnAction((event) -> {
            if (connectionPropertiesForm.isValid() & !connectionNameField.hasInvalidValueAndIfSoShowValidationMessage()) {
                val connectionData = connectionPropertiesForm.getConnectionData();

                if (connectionNameField.isActivated()) {
                    UserPreferences.push(DbConnection.from(
                            connectionData,
                            connectionNameField.getValue()
                    ));
                }

                stepper.next();
                stepper.fireEvent(new SiardEvent.DbmsConnectionDataReadyEvent(connectionData));
            }
        });

        this.buttonsBox.previous().setOnAction((event) -> stepper.previous());
        this.buttonsBox.cancel().setOnAction((event) -> stage.openDialog(View.ARCHIVE_ABORT_DIALOG));
    }
}
