package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.DialogPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class ArchiveDbDialogPresenter extends DialogPresenter {

    @FXML
    protected Label title;
    @FXML
    public Text text;
    @FXML
    protected MFXButton closeButton; // seems redundant
    @FXML
    public MFXButton newConnectionButton;
    @FXML
    protected HBox buttonBox;

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        title.textProperty().bind(I18n.createStringBinding("archiveDbDialog.title"));
        text.textProperty().bind(I18n.createStringBinding("archiveDbDialog.text"));

        newConnectionButton.textProperty().bind(I18n.createStringBinding("archiveDbDialog.btnNewConnection"));
        newConnectionButton.getStyleClass().setAll("button", "primary");

        newConnectionButton.setOnAction(event -> {
            stage.closeDialog();
            stage.navigate(View.ARCHIVE_STEPPER);
        });

        closeButton.setOnAction(event -> stage.closeDialog());

        buttonBox.getChildren().add(getCancelButton());
    }

}
