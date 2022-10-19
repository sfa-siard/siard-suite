package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.StepperButtonBox;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXStepper;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class ArchiveMetaDataPresenter  extends StepperPresenter {

    @FXML
    public Text title;

    @FXML Text description;

    @FXML
    Text titleWhat;

    @FXML
    Text titleWho;

    @FXML
    MFXTextField dbNameField;

    @FXML
    protected StepperButtonBox buttonsBox;

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        this.buttonsBox = new StepperButtonBox().make(StepperButtonBox.DEFAULT);
        this.borderPane.setBottom(buttonsBox);
    }

    @Override
    public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) {
        this.init(controller, model, stage);
        this.setListeners(stepper);
        this.bindTexts();
    }

    private void bindTexts() {
        this.title.textProperty().bind(I18n.createStringBinding("archiveMetadata.view.title"));
        this.description.textProperty().bind(I18n.createStringBinding("archiveMetadata.view.description"));
        this.titleWhat.textProperty().bind(I18n.createStringBinding("archiveMetadata.view.titleWhat"));
        this.titleWho.textProperty().bind(I18n.createStringBinding("archiveMetadata.view.titleWho"));

        this.dbNameField.floatingTextProperty().bind(I18n.createStringBinding("archiveConnection.view.dbName.label"));    }

    private void setListeners(MFXStepper stepper) {
        this.buttonsBox.next().setOnAction((event) -> stepper.next());
        this.buttonsBox.previous().setOnAction((event) -> {
            stepper.previous();
        });
        this.buttonsBox.cancel().setOnAction((event) -> stage.openDialog(View.ARCHIVE_ABORT_DIALOG.getName()));
    }
}
