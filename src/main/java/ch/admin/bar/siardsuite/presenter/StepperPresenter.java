package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.Step;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import ch.admin.bar.siardsuite.view.skins.CustomStepperToggleSkin;
import io.github.palexdev.materialfx.controls.MFXStepper;
import io.github.palexdev.materialfx.controls.MFXStepperToggle;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public abstract class StepperPresenter extends Presenter {

  @FXML
  protected BorderPane borderPane;
  public abstract void init(Controller controller, Model model, RootStage stage, MFXStepper stepper);

  public Event getUpdateEvent(EventType<SiardEvent> eventType) {
    return new SiardEvent(eventType);
  }

  protected List<MFXStepperToggle> createSteps(List<Step> steps, MFXStepper stepper) {

    return steps.stream()
            .map((step) -> createCustomStepperToggle(step.key(), step.position(), loadView(step.contentView(), stepper), step.visible()))
            .collect(Collectors.toList());
  }

  private MFXStepperToggle createCustomStepperToggle(String key, Integer pos, Node content, Boolean visible) {
    Button btn = new Button();
    btn.getStyleClass().setAll("stepper-btn", "number-btn");
    btn.setText(String.valueOf(pos));
    // passing the key is kind of a hack to bind it in the CustomStepperToggleSkin
    MFXStepperToggle toggle = new MFXStepperToggle(key, btn, content);
    toggle.setSkin(new CustomStepperToggleSkin(toggle, visible, stage));
    toggle.setVisible(visible);
    return toggle;

  }

  private Node loadView(String viewName, MFXStepper stepper) {
    try {
      FXMLLoader loader = new FXMLLoader(SiardApplication.class.getResource(viewName));
      Node container = loader.load();
      loader.<StepperPresenter>getController().init(this.controller, this.model, this.stage, stepper);
      return container;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
