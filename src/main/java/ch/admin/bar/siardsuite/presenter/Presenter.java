package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.application.Platform;
import javafx.stage.Stage;

public abstract class Presenter {

  protected Model model;
  protected Controller controller;
  protected Stage stage;

  public abstract void init(Controller controller, Model model, Stage stage);

  public void navigate(String view) {
    Platform.runLater(() -> {
      controller.navigate(view, (RootStage) this.stage);
    });
  }
}
