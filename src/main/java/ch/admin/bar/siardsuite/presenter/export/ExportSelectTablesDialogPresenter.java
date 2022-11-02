package ch.admin.bar.siardsuite.presenter.export;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.DialogPresenter;
import ch.admin.bar.siardsuite.ui.CloseDialogButton;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class ExportSelectTablesDialogPresenter extends DialogPresenter {
    @FXML
    public Label title;

    @FXML
    public MFXButton closeButton;

    @FXML
    public Text text;

    @FXML
    public HBox buttonBox;

    @FXML
    public MFXButton cancelButton;

    @FXML
    public MFXButton saveButton;

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        this.title.textProperty().bind(I18n.createStringBinding("export.select-tables.dialog.title"));
        this.text.textProperty().bind(I18n.createStringBinding("export.select-tables.dialog.text"));

        EventHandler<ActionEvent> closeEvent = event -> this.stage.closeDialog();
        this.closeButton.setOnAction(closeEvent);
        this.cancelButton.setOnAction(closeEvent);

        this.saveButton.setOnAction(this::handleSaveClicked);
    }

    private void handleSaveClicked(ActionEvent actionEvent) {
        // TODO: extract to object/ class or somewhere similar
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(I18n.get("export.file-chooser.title"));
        File file = directoryChooser.showDialog(stage);
        if (Objects.nonNull(file)) {
            try {
                this.model.getArchive().export(file);
                this.stage.openDialog(View.EXPORT_SUCCESS);
            } catch (Exception e) {
                // TODO: show failure message
                e.printStackTrace();
            } finally {
                this.stage.closeDialog();
            }
        }
    }
}


