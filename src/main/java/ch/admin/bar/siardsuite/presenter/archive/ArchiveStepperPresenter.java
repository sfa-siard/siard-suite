package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.ArchiveSteps;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.fxml.FXML;

public class ArchiveStepperPresenter extends StepperPresenter {

  @FXML
  private MFXStepper stepper;

  public void init(Controller controller, Model model, RootStage stage) {
    this.model = model;
    this.controller = controller;
    this.stage = stage;

    createStepper(controller, ArchiveSteps.steps, stepper);
  }

  @Override
  public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) { }
}
