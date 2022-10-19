package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.scene.layout.Pane;

public abstract class Presenter {

  protected Model model;
  protected Controller controller;
  protected RootStage stage;

  private double xOffset;
  private double yOffset;

  public abstract void init(Controller controller, Model model, RootStage stage);

  public void allowStageRepositioning(Pane pane) {
    pane.setOnMousePressed(event -> {
      xOffset = stage.getX() - event.getScreenX();
      yOffset = stage.getY() - event.getScreenY();
    });
    pane.setOnMouseDragged(event -> {
      stage.setX(event.getScreenX() + xOffset);
      stage.setY(event.getScreenY() + yOffset);
    });
  }
}
