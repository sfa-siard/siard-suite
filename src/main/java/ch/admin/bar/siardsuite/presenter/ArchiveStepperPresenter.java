package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.model.ArchiveViewSteps;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.view.skins.CustomStepperSkin;
import ch.admin.bar.siardsuite.view.skins.CustomStepperToggleSkin;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import io.github.palexdev.materialfx.controls.MFXStepperToggle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ArchiveStepperPresenter extends Presenter {

  @FXML
  private MFXButton unlock;

  @FXML
  private MFXStepper stepper;





  public void init(Controller controller, Model model, Stage stage) {
    this.model = model;
    this.controller = controller;
    this.stage = stage;

    List<MFXStepperToggle> stepperToggles = createSteps();

    stepper.getStepperToggles().addAll(stepperToggles);
    stepper.setSkin(new CustomStepperSkin(stepper));

  }

  private List<MFXStepperToggle> createSteps() {

    return  ArchiveViewSteps.getSteps().stream()
            .map((step) -> createCustomStepperToggle(step.getKey(), step.getPosition(), loadView(step.getContentView())))
            .collect(Collectors.toList());

//    step1.getValidator().dependsOn(loginField.getValidator()).dependsOn(passwordField.getValidator());

  }

  private MFXStepperToggle createCustomStepperToggle(String key, Integer pos, Node content) {
    Button btn = new Button();
    btn.getStyleClass().setAll("stepper-btn", "number-btn");
    if (pos > 1) {
      btn.setDisable(true);
    }
    btn.setText(String.valueOf(pos));
    // passing the key is kind of a hack to bind it in the CustomStepperToggleSkin
    MFXStepperToggle toggle =  new MFXStepperToggle(key, btn, content);
    toggle.setSkin(new CustomStepperToggleSkin(toggle));
    return toggle;
  }

  public Node loadView(String viewName) {
    try {
      FXMLLoader loader = new FXMLLoader(SiardApplication.class.getResource(viewName));
      Node container = loader.load();
      loader.<Presenter>getController().init(this.controller, this.model, this.stage);
      return container;
    } catch (IOException e) {
      System.out.println(e);
    }
    return null;
  }
}
