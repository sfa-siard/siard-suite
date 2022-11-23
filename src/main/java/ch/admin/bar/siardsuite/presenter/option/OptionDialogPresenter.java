package ch.admin.bar.siardsuite.presenter.option;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.CloseDialogButton;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.presenter.DialogPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.UserPreferences;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.time.Clock;
import java.util.Comparator;
import java.util.Objects;
import java.util.prefs.Preferences;

import static ch.admin.bar.siardsuite.util.UserPreferences.KeyIndex.*;
import static ch.admin.bar.siardsuite.util.UserPreferences.NodePath.OPTIONS;

public class OptionDialogPresenter extends DialogPresenter {


    @FXML
    protected Label title;
    @FXML
    protected Text text;
    @FXML
    protected MFXButton closeButton;

    @FXML
    protected HBox buttonBox;
    @FXML
    public MFXButton folderButton;
    @FXML
    public MFXTextField exportFolderText;

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        I18n.bind(title.textProperty(), "option.dialog.title");
        I18n.bind(text.textProperty(), "option.dialog.text");

        I18n.bind(exportFolderText.floatingTextProperty(), "option.dialog.export-path.label");
        I18n.bind(folderButton.textProperty(), "option.dialog.export-path.button");
        folderButton.setOnAction(this::handleSetExportPath);
        closeButton.setOnAction(event -> stage.closeDialog());
        buttonBox.getChildren().add(new CloseDialogButton(this.stage));

        initFormFields();

    }

    private void initFormFields() {
        final Preferences preferences = UserPreferences.node(OPTIONS);
        exportFolderText.setText(preferences.get(EXPORT_PATH.name(), ""));
    }

    private void handleSetExportPath(ActionEvent actionEvent) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(I18n.get("export.choose-location.text"));
        File file = directoryChooser.showDialog(stage);
        if (Objects.nonNull(file)) {
            try {
                final Preferences preferences = UserPreferences.push(OPTIONS, TIMESTAMP, Comparator.reverseOrder(), String.valueOf(file.hashCode()));
                preferences.put(EXPORT_PATH.name(), file.getAbsolutePath());
                preferences.put(TIMESTAMP.name(), String.valueOf(Clock.systemDefaultZone().millis()));
//                this.stage.openDialog(View.OPTION_DIALOG);
//                this.stage.closeDialog();

            } catch (Exception e) {
                // TODO: show failure message
                e.printStackTrace();
            }
        }
    }
}
