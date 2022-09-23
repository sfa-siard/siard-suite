package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.model.ViewStep;
import ch.admin.bar.siardsuite.view.skins.CustomStepperToggleSkin;
import io.github.palexdev.materialfx.controls.MFXStepper;
import io.github.palexdev.materialfx.controls.MFXStepperToggle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public abstract class StepperPresenter extends Presenter {

  public List<MFXStepperToggle> createSteps(List<ViewStep> steps) {

    return  steps.stream()
            .map((step) -> createCustomStepperToggle(step.getKey(), step.getPosition(), loadView(step.getContentView())))
            .collect(Collectors.toList());
//    step1.getValidator().dependsOn(loginField.getValidator()).dependsOn(passwordField.getValidator());
  }

  private MFXStepperToggle createCustomStepperToggle(String key, Integer pos, Node content) {
    Button btn = new Button();
    btn.getStyleClass().setAll("stepper-btn", "number-btn");
    btn.setText(String.valueOf(pos));
    // passing the key is kind of a hack to bind it in the CustomStepperToggleSkin
    MFXStepperToggle toggle =  new MFXStepperToggle(key, btn, content);
    toggle.setSkin(new CustomStepperToggleSkin(toggle));
    return toggle;
  }

  private Node loadView(String viewName) {
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
