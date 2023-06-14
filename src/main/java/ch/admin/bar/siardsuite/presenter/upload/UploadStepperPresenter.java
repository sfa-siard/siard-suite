package ch.admin.bar.siardsuite.presenter.upload;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.UploadSteps;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.fxml.FXML;

public class UploadStepperPresenter extends StepperPresenter {

    @FXML
    private MFXStepper stepper;

    public void init(Controller controller, RootStage stage) {
        this.controller = controller;
        this.stage = stage;

       createStepper(UploadSteps.steps, stepper);
    }

    @Override
    public void init(Controller controller, RootStage stage, MFXStepper stepper) { }
}
