package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.ArchiveSteps;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.view.RootStage;
import ch.admin.bar.siardsuite.view.skins.CustomStepperSkin;
import io.github.palexdev.materialfx.controls.MFXStepper;
import io.github.palexdev.materialfx.controls.MFXStepperToggle;
import javafx.fxml.FXML;
import javafx.scene.Node;

import java.util.List;

public class ArchiveStepperPresenter extends StepperPresenter {

  @FXML
  private MFXStepper stepper;

  public void init(Controller controller, Model model, RootStage stage) {
    this.model = model;
    this.controller = controller;
    this.stage = stage;

    List<MFXStepperToggle> stepperToggles = createSteps(ArchiveSteps.getSteps(), stepper);
    stepper.getStepperToggles().addAll(stepperToggles);
    stepper.setSkin(new CustomStepperSkin(stepper));
  }

  @Override
  public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) { }

}
