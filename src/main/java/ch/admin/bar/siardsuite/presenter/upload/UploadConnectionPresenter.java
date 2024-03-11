package ch.admin.bar.siardsuite.presenter.upload;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.component.ButtonBox;
import ch.admin.bar.siardsuite.component.SiardTooltip;
import ch.admin.bar.siardsuite.database.model.Dbms;
import ch.admin.bar.siardsuite.database.model.DbmsConnectionProperties;
import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.framework.steps.StepperNavigator;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.archive.browser.forms.utils.ListAssembler;
import ch.admin.bar.siardsuite.presenter.archive.model.DbmsWithInitialValue;
import ch.admin.bar.siardsuite.presenter.connection.ConnectionForm;
import ch.admin.bar.siardsuite.presenter.upload.model.ArchiveAdder;
import ch.admin.bar.siardsuite.presenter.upload.model.UploadArchiveData;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.fxml.FXMLLoadHelper;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.util.preferences.RecentDbConnection;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.val;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static ch.admin.bar.siardsuite.component.ButtonBox.Type.DEFAULT;

public class UploadConnectionPresenter {

    private static final I18nKey TOOLTIP = I18nKey.of("uploadConnection.view.tooltip");
    private static final I18nKey SCHEMA_NAME_TITLE = I18nKey.of("uploadConnection.view.new.schema.name");
    private static final I18nKey CURRENT_NAME = I18nKey.of("uploadConnection.view.current.name");
    private static final I18nKey NEW_NAME = I18nKey.of("uploadConnection.view.new.name");

    @FXML
    public BorderPane borderPane;
    @FXML
    public MFXButton infoButton;
    @FXML
    public Label titleNewSchemaName;
    @FXML
    public VBox schemaFields;
    @FXML
    public Label currentNameLabel;
    @FXML
    public Label newNameLabel;

    @FXML
    public ConnectionForm connectionForm;

    @FXML
    public Label schemaValidationMsg;


    private List<String> simpleSchemaNames;
    private final Map<String, String> schemaMap = new HashMap<>();

    public void init(
            final Dbms dbms,
            final Archive archive,
            final Optional<RecentDbConnection> initialValue,
            final StepperNavigator<UploadArchiveData> navigator,
            final ServicesFacade servicesFacade
    ) {
        simpleSchemaNames = ListAssembler.assemble(archive.getSchemas(), archive::getSchema).stream()
                .map(schema -> schema.getMetaSchema().getName())
                .collect(Collectors.toList());

        initSchemaFields();

        titleNewSchemaName.textProperty().bind(DisplayableText.of(SCHEMA_NAME_TITLE).bindable());
        currentNameLabel.textProperty().bind(DisplayableText.of(CURRENT_NAME).bindable());
        newNameLabel.textProperty().bind(DisplayableText.of(NEW_NAME).bindable());
        new SiardTooltip(TOOLTIP).showOnMouseOn(infoButton);

        val buttonsBox = new ButtonBox().make(DEFAULT);
        borderPane.setBottom(buttonsBox);

        connectionForm.show(dbms);

        initialValue.ifPresent(recentDbConnection -> {
            val dbmsConnectionData = recentDbConnection.mapToDbmsConnectionData();
            final DbmsConnectionProperties properties = dbmsConnectionData.getProperties();

            connectionForm.show(dbms, properties, recentDbConnection.getName());
        });

        buttonsBox.next()
                .setOnAction((event) -> {
                    val allPropsValid = connectionForm.isValid();
                    val validSchemaFields = validSchemaFields();

                    if (allPropsValid && validSchemaFields) {
                        val connectionData = connectionForm.getConnectionData();

                        navigator.next(UploadArchiveData.builder()
                                .schemaNameMappings(schemaMap)
                                .connectionData(connectionData)
                                .build());
                    }
                });

        buttonsBox.previous().setOnAction((event) -> navigator.previous());
        buttonsBox.cancel().setOnAction((event) -> servicesFacade
                .dialogs()
                .openDialog(View.UPLOAD_ABORT_DIALOG));
    }

    private boolean validSchemaFields() {
        if (hasValidSchemaFields()) return true;
        I18n.bind(schemaValidationMsg.textProperty(), "uploadConnection.view.error.schema.name");
        schemaValidationMsg.setVisible(true);
        return false;
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
        schemaFields.getChildren().clear();

        for (String schemaName : simpleSchemaNames) {
            Label currentName = new Label();
            Label iconLabel = new Label();
            TextField newName = new TextField();
            currentName.setText(schemaName);
            newName.setText(schemaName);
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

    public static LoadedFxml<UploadConnectionPresenter> load(
            final ArchiveAdder<DbmsWithInitialValue> data,
            final StepperNavigator<UploadArchiveData> navigator,
            final ServicesFacade servicesFacade
    ) {
        val loaded = FXMLLoadHelper.<UploadConnectionPresenter>load("fxml/upload/upload-db-connection.fxml");
        loaded.getController().init(
                data.getData().getDbms(),
                data.getArchive(),
                data.getData().getInitialValue(),
                navigator,
                servicesFacade);

        return loaded;
    }
}
