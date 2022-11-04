package ch.admin.bar.siardsuite.presenter.upload;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.Step;
import ch.admin.bar.siardsuite.model.UploadSteps;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.view.RootStage;
import ch.admin.bar.siardsuite.view.skins.CustomStepperSkin;
import io.github.palexdev.materialfx.controls.MFXStepper;
import io.github.palexdev.materialfx.controls.MFXStepperToggle;
import javafx.fxml.FXML;

import java.util.List;
import java.util.Objects;

public class UploadStepperPresenter extends StepperPresenter {

    @FXML
    private MFXStepper stepper;

    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        List<Step> steps = Objects.isNull(this.controller.recentDatabaseConnection) ? UploadSteps.steps : UploadSteps.savedConnectionSteps;
        List<MFXStepperToggle> stepperToggles = createSteps(steps, stepper);
        stepper.getStepperToggles().addAll(stepperToggles);
        stepper.setSkin(new CustomStepperSkin(stepper, stage));
    }

    @Override
    public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) { }
}
