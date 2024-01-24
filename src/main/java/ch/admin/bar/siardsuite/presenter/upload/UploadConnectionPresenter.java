package ch.admin.bar.siardsuite.presenter.upload;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.ButtonBox;
import ch.admin.bar.siardsuite.component.SiardTooltip;
import ch.admin.bar.siardsuite.database.model.Dbms;
import ch.admin.bar.siardsuite.database.model.DbmsConnectionProperties;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.model.database.DatabaseSchema;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.presenter.connection.ConnectionForm;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.val;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static ch.admin.bar.siardsuite.component.ButtonBox.Type.DEFAULT;

public class UploadConnectionPresenter extends StepperPresenter {

    private static final I18nKey TOOLTIP = I18nKey.of("uploadConnection.view.tooltip");
    private static final I18nKey SCHEMA_NAME_TITLE = I18nKey.of("uploadConnection.view.new.schema.name");
    private static final I18nKey CURRENT_NAME = I18nKey.of("uploadConnection.view.current.name");
    private static final I18nKey NEW_NAME = I18nKey.of("uploadConnection.view.new.name");

    @FXML
    public BorderPane borderPane;
    @FXML
    private ButtonBox buttonsBox;
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


    private List<DatabaseSchema> schemas = new ArrayList<>();
    private final Map<String, String> schemaMap = new HashMap<>();


    @Override
    public void init(Controller controller, RootStage stage) {
        this.controller = controller;
        this.stage = stage;
    }

    @Override
    public void init(Controller controller, RootStage stage, MFXStepper stepper) {
        this.init(controller, stage);

        schemas = controller.getSiardArchive().schemas();

        titleNewSchemaName.textProperty().bind(DisplayableText.of(SCHEMA_NAME_TITLE).bindable());
        currentNameLabel.textProperty().bind(DisplayableText.of(CURRENT_NAME).bindable());
        newNameLabel.textProperty().bind(DisplayableText.of(NEW_NAME).bindable());


        new SiardTooltip(DisplayableText.of(TOOLTIP)).showOnMouseOn(infoButton);
        buttonsBox = new ButtonBox().make(DEFAULT);
        borderPane.setBottom(buttonsBox);

        setListeners(stepper);
    }

    private void setListeners(MFXStepper stepper) {
        stepper.addEventHandler(SiardEvent.RECENT_CONNECTION_SELECTED_EVENT, event -> {
            val recentConnection = event.getRecentConnectionData();

            final Dbms dbms = recentConnection.mapToDbmsConnectionData().getDbms();
            final DbmsConnectionProperties properties = recentConnection.mapToDbmsConnectionData().getProperties();

            connectionForm.show(dbms, properties, recentConnection.getName());
        });

        stepper.addEventHandler(SiardEvent.UPLOAD_DBMS_SELECTED, event -> {
            connectionForm.show(event.getSelectedDbms());
            initSchemaFields();
        });

        buttonsBox.next().setOnAction((event) -> {
            val allPropsValid = connectionForm.isValid();
            val validSchemaFields = validSchemaFields();

            if (allPropsValid && validSchemaFields) {
                val connectionData = connectionForm.getConnectionData();

                controller.updateSchemaMap(schemaMap);

                stepper.next();
                stepper.fireEvent(new SiardEvent.DbmsConnectionDataReadyEvent(SiardEvent.UPLOAD_CONNECTION_UPDATED, connectionData));
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
}
