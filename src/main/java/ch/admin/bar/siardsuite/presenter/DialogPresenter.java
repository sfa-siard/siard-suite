package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

public class DialogPresenter extends Presenter {

  @FXML
  protected HBox windowHeader;

  @Override
  public void init(Controller controller, RootStage stage) {
    this.controller = controller;
    this.stage = stage;

    allowStageRepositioning(windowHeader);
  }
}
