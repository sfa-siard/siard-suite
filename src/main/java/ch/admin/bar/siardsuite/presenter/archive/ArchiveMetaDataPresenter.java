package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class ArchiveMetaDataPresenter  extends StepperPresenter {

    @FXML
    public Text title;

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;
    }

    @Override
    public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) {
        this.init(controller, model, stage);
        this.setListeners(stepper);
        this.bindTexts();
    }

    private void bindTexts() {
        this.title.textProperty().bind(I18n.createStringBinding("archiveMetadata.view.title"));
    }

    private void setListeners(MFXStepper stepper) {
        // todo
    }
}
